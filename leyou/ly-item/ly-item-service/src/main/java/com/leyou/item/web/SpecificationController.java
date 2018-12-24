package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/27
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {
	private final SpecificationService specificationService;

	@Autowired
	public SpecificationController(SpecificationService specificationService) {
		this.specificationService = specificationService;
	}

	/**
	 * 根据分类id查询规格组
	 * @param cid 分类id
	 * @return 规格组参数
	 */
	@GetMapping("groups/{cid}")
	public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
		return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
	}

	/**
	 * 根据id查询参数集合
	 * @param gid 组id
	 * @param cid 分类id
	 * @param searching 是否搜索
	 * @return 参数集合
	 */
	@GetMapping("params")
	public ResponseEntity<List<SpecParam>> queryParam(
			@RequestParam(value = "gid",required = false)Long gid,
			@RequestParam(value = "cid",required = false)Long cid,
			@RequestParam(value = "searching",required = false) Boolean searching

	){
		return ResponseEntity.ok(specificationService.queryParamByGid(gid,cid,searching));
	}
}