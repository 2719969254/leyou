package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/18
 */
@Data
@Table(name="tb_category")
public class Category {
	@Id
	@KeySql(useGeneratedKeys=true)
	private Long id;
	private String name;
	private Long parentId;
	/**
	 * 注意isParent生成的getter和setter方法需要手动加上Is
	 */
	private Boolean isParent;
	private Integer sort;
	// getter和setter略
}