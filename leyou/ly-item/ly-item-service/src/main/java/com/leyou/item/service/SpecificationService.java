package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/27
 */
@Service
public class SpecificationService {
	private final SpecGroupMapper specGroupMapper;
	private final SpecParamMapper specParamMapper;

	@Autowired
	public SpecificationService(SpecGroupMapper specGroupMapper, SpecParamMapper specParamMapper) {
		this.specGroupMapper = specGroupMapper;
		this.specParamMapper = specParamMapper;
	}


	public List<SpecGroup> queryGroupByCid(Long cid) {
		//查询条件
		SpecGroup specGroup = new SpecGroup();
		specGroup.setCid(cid);
		//开始查询
		List<SpecGroup> list = specGroupMapper.select(specGroup);
		if (CollectionUtils.isEmpty(list)) {
			//如果没查到，则抛出异常
			throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
		}
		return list;
	}

	public List<SpecParam> queryParamByGid(Long gid, Long cid, Boolean searching) {
		SpecParam specParam = new SpecParam();
		specParam.setGroupId(gid);
		specParam.setCid(cid);
		specParam.setSearching(searching);
		List<SpecParam> list = specParamMapper.select(specParam);
		if (CollectionUtils.isEmpty(list)) {
			throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
		}
		return list;
	}
}
