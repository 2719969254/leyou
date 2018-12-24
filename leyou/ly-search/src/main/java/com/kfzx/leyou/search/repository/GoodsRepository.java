package com.kfzx.leyou.search.repository;

import com.kfzx.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/22
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
