package com.kfzx.leyou.service;

import com.kfzx.leyou.mapper.UserMapper;
import com.kfzx.leyou.sms.config.SmsProperties;
import com.kfzx.leyou.user.pojo.User;
import com.kfzx.leyou.utils.CodecUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/4
 */
@Slf4j
@Service
public class UserService {
	private final UserMapper userMapper;
	private final AmqpTemplate amqpTemplate;
	private final SmsProperties smsProperties;
	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public UserService(UserMapper userMapper, AmqpTemplate amqpTemplate, SmsProperties smsProperties, RedisTemplate<String, String> redisTemplate) {
		this.userMapper = userMapper;
		this.amqpTemplate = amqpTemplate;
		this.smsProperties = smsProperties;
		this.redisTemplate = redisTemplate;
	}

	public Boolean checkData(String data, Integer type) {
		User record = new User();
		switch (type) {
			case 1:
				record.setUsername(data);
				break;
			case 2:
				record.setPhone(data);
				break;
			default:
				throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
		}
		return userMapper.selectCount(record) == 0;
	}

	public Boolean sendVerifyCode(String phone) {
		try {
			// 发送短信
			Map<String, String> msg = new HashMap<>(1);
			msg.put("phone", phone);
			amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);
			return true;
		} catch (Exception e) {
			log.error("发送短信失败。phone：{}", phone);
			return false;
		}
	}

	public Boolean register(User user, String code) {
		String key = smsProperties.getKeyPerfix() + user.getPhone();
		// 从redis中取出验证码
		String cacheCode = redisTemplate.opsForValue().get(key);
		if (StringUtils.equals(code, cacheCode)) {
			throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
		}
		user.setId(null);
		user.setCreated(new Date());
		// 生成盐
		String salt = CodecUtils.generateSalt();
		user.setSalt(salt);
		// 对密码进行加密
		user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
		// 写入数据库
		boolean boo = this.userMapper.insertSelective(user) == 1;

		// 如果注册成功，删除redis中的code
		if (boo) {
			try {
				this.redisTemplate.delete(key);
			} catch (Exception e) {
				log.error("删除缓存验证码失败，code：{}", code, e);
			}
		}
		return boo;
	}
	public User queryByUsernameAndPassword(String username,String password){
		// 查询用户
		User record = new User();
		record.setUsername(username);
		User user = userMapper.selectOne(record);
		if (user == null){
			throw new LyException(ExceptionEnum.INVALID_USER);
		}
		// 校验密码
		if(!StringUtils.equals(user.getPassword(),CodecUtils.md5Hex(password,user.getSalt()))){
			throw new LyException(ExceptionEnum.INVALID_USER);
		}
		return user;
	}
}
