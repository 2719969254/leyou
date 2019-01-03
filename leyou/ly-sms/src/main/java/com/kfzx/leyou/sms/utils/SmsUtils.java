package com.kfzx.leyou.sms.utils;

import com.kfzx.leyou.sms.config.SmsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author MR.Tian
 */
@Component
public class SmsUtils {
	private final SmsProperties smsProperties;

	@Autowired
	public SmsUtils(SmsProperties smsProperties) {
		this.smsProperties = smsProperties;
	}


	/**
	 * 根据相应的手机号发送验证码
	 */
	public String sendCode(String telephone, String signName) {
		try {
			String rod = smsCode();
			String timestamp = getTimestamp();
			String sig = getMD5(smsProperties.getAccountSid(), smsProperties.getAuthToken(), timestamp);
			int time = 5;
			//这里一定要与新建模板中的短信内容一致，一个空格都不能多，否者短信打死都发不过去
			String tamp = signName + "您的验证码为" + rod + "，请于" + time + "分钟内正确输入，如非本人操作，请忽略此短信。";
			OutputStreamWriter out;
			BufferedReader br;
			StringBuilder result = new StringBuilder();
			try {
				URL url = new URL(smsProperties.getQueryPath());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				//设置是否允许数据写入
				connection.setDoInput(true);
				//设置是否允许参数数据输出
				connection.setDoOutput(true);
				//设置链接响应时间
				connection.setConnectTimeout(5000);
				//设置参数读取时间
				connection.setReadTimeout(10000);
				connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				//提交请求
				out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
				String args = getQueryArgs(smsProperties.getAccountSid(), tamp, telephone, timestamp, sig);
				out.write(args);
				out.flush();
				//读取返回参数

				br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
				String temp;
				while ((temp = br.readLine()) != null) {
					result.append(temp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject json;
			String respCode = null;
			try {
				json = new JSONObject(result.toString());
				respCode = json.getString("respCode");
			} catch (JSONException e) {
				e.printStackTrace();
			}


			String defaultRespCode = "00000";
			if (defaultRespCode.equals(respCode)) {
				return rod;
			} else {
				return defaultRespCode;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 定义一个请求参数拼接方法
	 */
	private static String getQueryArgs(String accountSid, String smsContent, String to, String timestamp, String sig) {
		return "accountSid=" + accountSid + "&smsContent=" + smsContent + "&to=" + to + "&timestamp=" + timestamp + "&sig=" + sig + "&respDataType=" + "JSON";
	}

	/**
	 * 获取时间戳
	 */
	private static String getTimestamp() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	/**
	 * sing签名
	 */
	private static String getMD5(String sid, String token, String timestamp) {

		StringBuilder result = new StringBuilder();
		String source = sid + token + timestamp;
		//获取某个类的实例
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//要进行加密的东西
			byte[] bytes = digest.digest(source.getBytes());
			for (byte b : bytes) {
				String hex = Integer.toHexString(b & 0xff);
				if (hex.length() == 1) {
					result.append("0").append(hex);
				} else {
					result.append(hex);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}


		return result.toString();
	}

	/**
	 * 创建验证码
	 */
	private static String smsCode() {
		return (int) ((Math.random() * 9 + 1) * 100000) + "";
	}
}