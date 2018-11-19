package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/19
 */
@Data
@Table(name = "tb_brand")
public class Brand {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Long id;
	private String name;
	private String image;
	private Character letter;
}
