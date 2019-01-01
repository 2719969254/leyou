package com.leyou.item.bo;

import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Sku;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @author: 98050
 * Time: 2018-08-14 22:10
 * Feature:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpuBo extends Spu {
    /**
     * 商品分类名称
     */
    @Transient
    private String cname;
    /**
     * 品牌名称
     */
    @Transient
    private String bname;

    /**
     * 商品详情
     */
    @Transient
    private SpuDetail spuDetail;

    /**
     * sku列表
     */
    @Transient
    private List<Sku> skus;

}
