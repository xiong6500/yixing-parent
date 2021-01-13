package com.yixing.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yixing.entity.PageResult;
import com.yixing.mapper.TbItemCatMapper;
import com.yixing.pojo.TbItemCat;
import com.yixing.pojo.TbItemCatExample;
import com.yixing.pojo.TbItemCatExample.Criteria;
import com.yixing.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

/**
 * 商品类目服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCatExample catExample = new TbItemCatExample();
        Criteria criteria = catExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatMapper.selectByExample(catExample);
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbItemCat itemCat) {
        itemCatMapper.updateByPrimaryKey(itemCat);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbItemCat findOne(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        TbItemCatExample catExample = new TbItemCatExample();
        Criteria criteria = catExample.createCriteria();
        for (Long id : ids) {
            itemCatMapper.deleteByPrimaryKey(id);
            criteria.andParentIdEqualTo(id);
            itemCatMapper.deleteByExample(catExample);
        }
    }


    @Override
    public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbItemCatExample example = new TbItemCatExample();
        Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (itemCat.getParentId() != null) {
                criteria.andParentIdEqualTo(itemCat.getParentId());
            }
        }
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        //每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
        final List<TbItemCat> itemCatList = findAll();
        System.out.println(itemCatList.size());
        //高效插入
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (TbItemCat itemCat : itemCatList) {
                    redisTemplate.opsForHash().put("itemCat", itemCat.getName(), itemCat.getTypeId());
                }
                return null;
            }
        });
        System.out.println("更新缓存:商品分类表");
        return new PageResult(page.getTotal(), page.getResult());
    }

}
