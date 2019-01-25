package com.leyou.auth.web;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MR.Tian
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

	private final AuthService authService;

	private final JwtProperties props;

	@Autowired
	public AuthController(AuthService authService, JwtProperties props) {
		this.authService = authService;
		this.props = props;
	}

	/**
	 * 登录授权
	 *
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("accredit")
	public ResponseEntity<Void> login(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		String token = authService.login(username, password);
		if (StringUtils.isBlank(token)) {
			throw new LyException(ExceptionEnum.INVALID_USER);
		}
		//将Token写入cookie中
		CookieUtils.newBuilder(response).httpOnly().maxAge(props.getCookieMaxAge()).request(request).build(props.getCookieName(), token);
		return ResponseEntity.ok().build();
	}

	/**
	 * 验证用户信息
	 *
	 * @param token
	 * @return
	 */
	@GetMapping("verify")
	public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {
		try {
			//从Token中获取用户信息
			UserInfo userInfo = JwtUtils.getUserInfo(props.getPublicKey(), token);
			//成功，刷新Token
			String newToken = JwtUtils.generateToken(userInfo, props.getPrivateKey(), props.getExpire());
			//将新的Token写入cookie中，并设置httpOnly
			CookieUtils.newBuilder(response).httpOnly().maxAge(props.getCookieMaxAge()).request(request).build(props.getCookieName(), newToken);
			return ResponseEntity.ok(userInfo);
		} catch (Exception e) {
			//Token无效
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}
}