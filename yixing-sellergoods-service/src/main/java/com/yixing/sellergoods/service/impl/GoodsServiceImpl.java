package com.yixing.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yixing.entity.PageResult;
import com.yixing.group.Goods;
import com.yixing.mapper.*;
import com.yixing.pojo.*;
import com.yixing.pojo.TbGoodsExample.Criteria;
import com.yixing.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> list = itemMapper.selectByExample(example);
            for (TbItem tbItem : list) {
                tbItem.setStatus("1");
                itemMapper.updateByPrimaryKey(tbItem);
            }
        }
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0");//设置未申请状态
        goods.getGoods().setIsDelete("0");//表示未删除
        goodsMapper.insert(goods.getGoods());
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
        saveItemList(goods);
    }

    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {//开启规格
            for (TbItem tbItem : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec(), Map.class);
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                tbItem.setTitle(title);

                setItemValues(goods, tbItem);

                itemMapper.insert(tbItem);
            }
        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());
            tbItem.setPrice(goods.getGoods().getPrice());//价格
            tbItem.setStatus("1");//状态
            tbItem.setIsDefault("1");//是否默认
            tbItem.setNum(99999);//库存数量
            tbItem.setSpec("{}");
            setItemValues(goods, tbItem);
            itemMapper.insert(tbItem);
        }
    }

    private void setItemValues(Goods goods, TbItem tbItem) {
        tbItem.setGoodsId(goods.getGoods().getId());//设置SPUid
        Long category3Id = goods.getGoods().getCategory3Id();
        tbItem.setCategoryid(category3Id);//设置分类id
        tbItem.setCategory(itemCatMapper.selectByPrimaryKey(category3Id).getName());//设置分类名称
        tbItem.setSellerId(goods.getGoods().getSellerId());//设置商家id
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(brand.getName());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(seller.getNickName());
        //图片地址
        List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (maps.size() > 0) {
            tbItem.setImage((String) maps.get(0).get("url"));
        }
        tbItem.setCreateTime(new Date());//创建日期
        tbItem.setUpdateTime(new Date());//更新日期
    }

    /**
     * 修改
     *
     * @param goods
     */
    @Override
    public void update(Goods goods) {
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        List<TbItem> itemList = goods.getItemList();
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(itemExample);
        saveItemList(goods);//插入商品SKU列表数据
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(goodsDesc);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            criteria.andIsDeleteEqualTo("0");
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
