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
	PRICE_CANNOT_BE_NULL(400,"价格并不能为空"),
	CATEGORY_NOT_FOND(404,"商品分类没查到"),
	BRAND_NOT_FOUND(404,"品牌没查到"),
	;
	private Integer code;
	private String msg;
}
