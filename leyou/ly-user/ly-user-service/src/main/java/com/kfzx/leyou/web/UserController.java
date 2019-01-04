package com.kfzx.leyou.web;

import com.kfzx.leyou.service.UserService;
import com.kfzx.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/4
 */
@RestController
public class UserController {
	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("check/{data}/{type}")
	public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
		Boolean boo = userService.checkData(data, type);
		if (boo == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(boo);
	}

	/**
	 * 发送手机验证码
	 *
	 * @param phone 电话
	 * @return Void
	 */
	@PostMapping("code")
	public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
		Boolean boo = this.userService.sendVerifyCode(phone);
		if (boo == null || !boo) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * 注册
	 *
	 * @param user 用户对象
	 * @param code 验证码
	 * @return Void
	 */
	@PostMapping("register")
	public ResponseEntity<Void> register(@Valid User user, BindingResult bindingResult, @RequestParam("code") String code) {
		if (bindingResult.hasErrors()) {
			throw new RuntimeException(bindingResult.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("|")));
		}
		Boolean boo = this.userService.register(user, code);
		if (boo == null || !boo) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
