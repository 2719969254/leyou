package com.kfzx.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 用户名
	 */
	@NotEmpty(message = "用户名不能为空")
	@Length(min = 4, max = 30, message = "用户名只能在4~30位之间")
	private String username;
	/**
	 * 密码
	 */
	@JsonIgnore
	@Length(min = 4, max = 30, message = "用户名只能在4~30位之间")
	private String password;
	/**
	 * 电话
	 */
	@Pattern(regexp = "^1[35678]\\d{9}$", message = "手机号格式不正确")
	private String phone;
	/**
	 * 创建时间
	 */
	private Date created;
	/**
	 * 密码的盐值
	 */
	@JsonIgnore
	private String salt;
}
