package com.yixing.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 删除数据
     * @param goodsIdList
     */
    public void deleteByGoodsIds(List goodsIdList);

    /**
     * 将商品数据导入solr
     * @param list
     */
    public void importList(List list);

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);
}
