package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/18
 */
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {

}
