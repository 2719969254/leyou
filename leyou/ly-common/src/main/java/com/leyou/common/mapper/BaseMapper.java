package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/30
 */
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>, InsertListMapper<T>, IdListMapper<T,Long> {
}
