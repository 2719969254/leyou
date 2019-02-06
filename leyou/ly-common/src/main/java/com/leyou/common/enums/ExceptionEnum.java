package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author MR.Tian
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
	/**
	 * 价格不能为空
	 */
	PRICE_CANNOT_BE_NULL(400, "价格并不能为空"),
	CATEGORY_NOT_FOND(404, "商品分类没查到"),
	BRAND_NOT_FOUND(404, "品牌没查到"),
	GOODS_DETAIL_NOT_FOUND(404, "商品详情没查到"),
	GOODS_NOT_FOUND(404, "品牌没查到"),
	BRAND_SAVE_ERROR(500, "新增品牌失败"),
	CATEGORY_BRAND_SAVE_ERROR(500, "新增品牌失败"),
	GOODS_SAVE_ERROR(500, "新增商品失败"),
	UPLOAD_FILE_ERROR(500, "文件上传失败"),
	INVALID_FILE_TYPE(400, "无效文件类型"),
	SPEC_GROUP_NOT_FOUND(404, "商品规格组不存在"),
	SPEC_PARAM_NOT_FOUND(404, "商品规格不存在"),
	GOOD_SPU_NOT_FOUND(404, "商品SPU不存在"),
	GOOD_STOCK_NOT_FOUND(404, "商品库存不存在"),
	GOOD_SKU_NOT_FOUND(404, "商品SKU不存在"),
	GOODS_NOT_SALEABLE(404, "商品未上架"),
	GOODS_UPDATE_ERROR(500, "商品修改失败"),
	INVALID_USER_DATA(400, "用户数据类型不正确"),
	INVALID_VERIFY_CODE(400, "无效验证码"),
	INVALID_USER(400, "用户名或密码错误"),
	CART_NOT_FOUND(404,"购物车为空"),
	CREATE_ORDER_ERROR(500,"创建订单失败"),
	STOCK_NOT_ENOUGH(500,"库存量不足"),
	WXPAY_ORDER_ERROR(500,"微信下单失败"),
	INVALID_SING_ERROR(400,"无效的签名异常"),
	INVALID_ORDER_PARAM_ERROR(400,"订单参数异常"),
	UPDATE_ORDER_STATUS_ERROR(500,"更新订单状态异常"),
	ORDER_NOT_FOUND(404,"订单不存在"),
	ORDER_DETAIL_NOT_FOUND(404,"订单详情不存在"),
	ORDER_STATUS_NOT_FOUND(404,"订状态不存在"),
	;
	private Integer code;
	private String msg;
}