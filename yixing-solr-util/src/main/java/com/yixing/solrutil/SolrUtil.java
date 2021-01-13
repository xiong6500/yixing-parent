package com.yixing.solrutil;

import com.alibaba.fastjson.JSON;
import com.yixing.mapper.TbItemMapper;
import com.yixing.pojo.TbItem;
import com.yixing.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = applicationContext.getBean("solrUtil", SolrUtil.class);
        solrUtil.importItemData();
    }

    /**
     * 导入商品数据
     */
    public void importItemData(){
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
        for (TbItem tbItem : tbItems) {
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }
}
