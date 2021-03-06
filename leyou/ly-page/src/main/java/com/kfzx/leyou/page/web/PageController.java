package com.kfzx.leyou.page.web;

import com.kfzx.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class PageController {
    private final PageService pageService;

    @Autowired
    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    /**
     * 跳转到商品详情页
     * @param model
     * @param spuId
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long spuId){
        Map<String,Object> attributes = pageService.loadModel(spuId);
        model.addAllAttributes(attributes);
        return "item";
    }
}