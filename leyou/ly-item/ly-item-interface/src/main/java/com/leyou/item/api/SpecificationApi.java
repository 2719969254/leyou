package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
public interface SpecificationApi {
	/**
	 * 根据id查询参数集合
	 * @param gid 组id
	 * @param cid 分类id
	 * @param searching 是否搜索
	 * @return 参数集合
	 */
	@GetMapping("spec/params")
	List<SpecParam> queryParam(
			@RequestParam(value = "gid",required = false)Long gid,
			@RequestParam(value = "cid",required = false)Long cid,
			@RequestParam(value = "searching",required = false) Boolean searching
	);
	@GetMapping("spec/group")
	List<SpecGroup> queryGroupByCid(@PathVariable("cid") Long cid);
}
