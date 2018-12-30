package com.kfzx.leyou.search.repository;

import com.kfzx.leyou.search.client.GoodsClient;
import com.kfzx.leyou.search.pojo.Goods;
import com.kfzx.leyou.search.service.SearchService;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
	@Autowired
	private GoodsRepository goodsRepository;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private GoodsClient goodsClient;
	@Autowired
	private SearchService searchService;
	@Test
	public void testCreateIndex(){
		elasticsearchTemplate.createIndex(Goods.class);
		elasticsearchTemplate.putMapping(Goods.class);
		// elasticsearchTemplate.deleteIndex(Goods.class);
	}

	@Test
	public void loadData(){
		int page = 1;
		int rows = 100;
		int size = 0;
		do {
			// 查询分页数据
			PageResult<Spu> result = this.goodsClient.querySpuByPage(page, rows, true, null);
			List<Spu> spus = result.getItems();
			size = spus.size();
			// 创建Goods集合
			List<Goods> goodsList = new ArrayList<>();
			// 遍历spu
			for (Spu spu : spus) {
				try {
					Goods goods = this.searchService.buildGoods(spu);
					goodsList.add(goods);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}

			this.goodsRepository.saveAll(goodsList);
			page++;
		} while (size == 100);
	}


}
