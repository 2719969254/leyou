package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.dto.OrderDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.interceptor.UserInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper detailMapper;

    @Autowired
    private OrderStatusMapper statusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private PayHelper payHelper;

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        //1.新增订单
        Order order = new Order();
        //1.1 订单编号  基本信息
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        //1.2 用户信息
        UserInfo user = LoginInterceptor.getLoginUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getName());
        order.setBuyerRate(false);
        //1.3 收件人地址
        AddressDTO addressDTO = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverDistrict(addressDTO.getDistrict());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverState(addressDTO.getState());
        order.setReceiverZip(addressDTO.getZipCode());
        //1.4 金额
        Map<Long, Integer> numMap = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId,CartDTO::getNum));
        List<Long> ids =new ArrayList<>(numMap.keySet());
        List<Sku> skus = goodsClient.querySkusBuSkuIds(ids);
        long totalPay = 0L;
        //准备orderDetail集合
        List<OrderDetail> details = new ArrayList<>();
        for (Sku sku : skus) {
            //计算总价
            totalPay += sku.getPrice()*numMap.get(sku.getId());
            //封装orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(numMap.get(sku.getId()));
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setOrderId(orderId);
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        order.setActualPay(totalPay + order.getPostFee() - 0);
        //1.5 order写入数据库
        int orderCount = orderMapper.insertSelective(order);
        if(orderCount != 1){
            log.error("[订单服务] 创建订单失败 orderId={}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //2.新增订单详情
        int detailCount = detailMapper.insertList(details);
        if(detailCount != details.size()){
            log.error("[订单服务] 创建订单失败 orderId={}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //3.新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        int statusCount = statusMapper.insertSelective(orderStatus);
        if(statusCount != 1){
            log.error("[订单服务] 创建订单失败 orderId={}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //4.减库存
	    List<CartDTO> carts = orderDTO.getCarts();
	    goodsClient.decreaseStock(carts);
        return orderId;
    }

    public Order queryOrderById(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> details = detailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(details)) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(details);
        //查询订单状态
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public String createPayUrl(Long orderId) {
        Order order = queryOrderById(orderId);
        //获取实付金额
        Long actualPay = /*order.getActualPay()*/ 1L;
        //获取描述
        String desc = order.getOrderDetails().get(0).getTitle();
        return payHelper.createOrders(orderId, actualPay, desc);
    }

    public void handleNotify(Map<String, String> result) {
        //1.数据校验
        payHelper.isSuccess(result);

        //2.签名校验
        payHelper.isValidSign(result);

        //3.校验金额
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM_ERROR);
        }
        //3.1  获取结果中的金额
        Long resultPay = Long.valueOf(totalFeeStr);
        //3.2  获取订单中的金额
        Long tradeId = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(tradeId);
        if (/*order.getActualPay()*/1L != resultPay) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM_ERROR);
        }

        //4.修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(tradeId);
        orderStatus.setStatus(OrderStatusEnum.PAYED.value());
        orderStatus.setPaymentTime(new Date());
        int count = statusMapper.updateByPrimaryKeySelective(orderStatus);
        if(count != 1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("[订单回调], 订单支付成功！ 订单编号:{}",tradeId);
    }

    public PayState queryPayStateByOrderId(Long orderId) {
        //查询订单
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(orderId);
        //状态为不是未支付一定支付成功
        if (orderStatus.getStatus() != OrderStatusEnum.UN_PAY.value()) {
            return PayState.SUCCESS;
        }
        //状态为未支付不一定支付失败
        return payHelper.queryPayState(orderId);
    }
}
