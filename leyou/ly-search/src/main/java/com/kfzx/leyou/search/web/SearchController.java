package com.kfzx.leyou.search.web;

import com.kfzx.leyou.search.pojo.Goods;
import com.kfzx.leyou.search.pojo.SearchRequest;
import com.kfzx.leyou.search.service.SearchService;
import com.leyou.common.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/12/24
 */
@RestController
@RequestMapping
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 搜索商品
     *
     * @param request 请求
     * @return ResponseEntity<PageResult<Goods>>
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request) {
        PageResult<Goods> result = this.searchService.search(request);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}