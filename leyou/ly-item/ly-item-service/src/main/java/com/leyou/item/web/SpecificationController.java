package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
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
	 * @param cid
	 * @return
	 */
	@GetMapping("groups/{cid}")
	public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
		return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
	}

	/**
	 * 根据组id查询参数
	 * @param gid
	 * @return
	 */
	@GetMapping("params")
	public ResponseEntity<List<SpecParam>> queryParamByCid(@RequestParam("gid")Long gid){
		return ResponseEntity.ok(specificationService.queryParamByGid(gid));
	}
}