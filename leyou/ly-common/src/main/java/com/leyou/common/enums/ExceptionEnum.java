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
	GOODS_NOT_FOUND(404,"品牌没查到"),
	BRAND_SAVE_ERROR(500,"新增品牌失败"),
	CATEGORY_BRAND_SAVE_ERROR(500,"新增品牌失败"),
	UPLOAD_FILE_ERROR(500,"文件上传失败"),
	INVALID_FILE_TYPE(400,"无效文件类型"),
	SPEC_GROUP_NOT_FOUND(404,"商品规格组不存在"),
	SPEC_PARAM_NOT_FOUND(404,"商品规格不存在"),
	;
	private Integer code;
	private String msg;
}