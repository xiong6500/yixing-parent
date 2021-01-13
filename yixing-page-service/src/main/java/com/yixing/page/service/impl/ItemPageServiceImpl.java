package com.yixing.page.service.impl;

import com.yixing.mapper.TbGoodsDescMapper;
import com.yixing.mapper.TbGoodsMapper;
import com.yixing.mapper.TbItemCatMapper;
import com.yixing.mapper.TbItemMapper;
import com.yixing.page.service.ItemPageService;
import com.yixing.pojo.TbGoods;
import com.yixing.pojo.TbGoodsDesc;
import com.yixing.pojo.TbItem;
import com.yixing.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean getItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap();
            //1.加载商品表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //2.加载商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);
            Writer out = new FileWriter(pagedir+goods.getId()+".html");
            //3.商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //SKU列表
            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria criteria = itemExample.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");//状态有效
            itemExample.setOrderByClause("is_default desc");//按照状态降序,保证第一个为默认
            List<TbItem> itemList = itemMapper.selectByExample(itemExample);
            dataModel.put("itemList",itemList);

            template.process(dataModel,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                File file = new File(pagedir+goodsId+".html");
                if (file.exists()){
                    file.delete();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
