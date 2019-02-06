package com.leyou.order.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MR.Tian
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

	private JwtProperties jwtProp;

	private static final ThreadLocal<UserInfo> tl  = new ThreadLocal<>();

	public UserInterceptor(JwtProperties jwtProp) {
		this.jwtProp = jwtProp;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String token = CookieUtils.getCookieValue(request, jwtProp.getCookieName());
		try{
			//解析token
			UserInfo user = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
			//传递user
			tl.set(user);
			//放行
			return true;
		}catch (Exception e){
			log.error("[购物车服务]  解析用户身份失败" , e);
			return false;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		tl.remove();
	}

	public static UserInfo getUser(){
		return tl.get();
	}
}
