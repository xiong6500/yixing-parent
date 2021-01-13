package com.yixing.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yixing.pojo.TbItem;
import com.yixing.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 在redis中查询品牌和规格列表
     *
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map<String, Object> map = new HashMap<>();
        Long typeId = (Long) redisTemplate.opsForHash().get("itemCat", category);
        if (typeId != null) {
            //查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//存入map中
            //查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }

    /**
     * 高亮查询
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        //高亮显示查询
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        //高亮显示查询项(域)
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);
        String keywords = (String) searchMap.get("keywords");
        //1.1根据复制域查询,会根据多个域查询
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        highlightQuery.addCriteria(criteria);
        //1.2按分类查询
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //1.3按品牌查询
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //1.4按规格查询
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //1.5按价格查询
        String price = (String) searchMap.get("price");
        if (!"".equals(price)) {
            String[] split = price.split("-");
            if (!"0".equals(split[0])) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
            if (!"*".equals(split[1])) {
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //1.6  分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
        if (pageNo == null) {
            pageNo = 1;//默认一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;//默认20
        }
        highlightQuery.setOffset((pageNo - 1) * pageSize);
        highlightQuery.setRows(pageSize);

        //按照价格排序
        String sortValue = (String) searchMap.get("sortValue");
        String sortFiled = (String) searchMap.get("sortFiled");

        if (StringUtils.isNotEmpty(sortValue)) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortFiled);
                highlightQuery.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortFiled);
                highlightQuery.addSort(sort);
            }
        }
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {//循环高亮入口集合
            TbItem item = h.getEntity();//获取原实体类
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //酷派 锋尚 Y75 白色 移动4G手机
                //<em style='color:red'>酷派</em>锋尚 Y75 白色 移动4G手机
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }

        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());
        map.put("total", page.getTotalElements());
        return map;
    }

    /**
     * 查询商品的分类列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //按照分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
//        list = content.stream().map(item -> item.getGroupValue()).collect(Collectors.toList());
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //将分组结果的名称封装到返回值中
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        //1.根据关键字高亮查询
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3.查询品牌和规格列表

        if (!"".equals(searchMap.get("category"))) {
            map.putAll(searchBrandAndSpecList((String) searchMap.get("category")));
        } else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }
}
