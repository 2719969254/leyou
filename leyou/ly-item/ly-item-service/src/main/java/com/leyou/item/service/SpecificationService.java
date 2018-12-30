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

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public List<SpecGroup> queryListByCid(Long cid) {
		//查询规格组
		List<SpecGroup> specGroups = queryGroupByCid(cid);
		//查询当前分类下的参数
		List<SpecParam> specParams = queryParamByGid(null, cid, null);
		//先把规格参数变为map，map的key是规格组id，map的值是组下所有参数
		Map<Long,List<SpecParam>> map = new HashMap<>(100);
		for (SpecParam param : specParams) {
			if (!map.containsKey(param.getGroupId())){
				//这个组是第一次出现
				map.put(param.getGroupId(),new ArrayList<>());
			}
			//不管是不是第一次出现，都要进行数据填充
			map.get(param.getGroupId()).add(param);
		}

		//填充param到group里
		for (SpecGroup specGroup : specGroups) {
			specGroup.setParams(specParams);
		}
		//填充
		return specGroups;
	}
}
