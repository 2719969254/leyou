package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/27
 */
@Table(name = "tb_spec_param")
@Data
public class SpecParam {
	@Id
	@KeySql(useGeneratedKeys = true)
	private Long id;
	private Long cid;
	private Long groupId;
	private String name;
	@Column(name="`numeric`")
	private Boolean numeric;
	private String unit;
	private Boolean generic;
	private Boolean searching;
	private String segments;
}
