package com.yixing.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yixing.entity.PageResult;
import com.yixing.mapper.TbSpecificationOptionMapper;
import com.yixing.mapper.TbTypeTemplateMapper;
import com.yixing.pojo.TbSpecificationOption;
import com.yixing.pojo.TbSpecificationOptionExample;
import com.yixing.pojo.TbTypeTemplate;
import com.yixing.pojo.TbTypeTemplateExample;
import com.yixing.pojo.TbTypeTemplateExample.Criteria;
import com.yixing.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 存储品牌和规格到redis中
     */
    private void saveToRedis() {
        List<TbTypeTemplate> templateList = findAll();
        for (TbTypeTemplate typeTemplate : templateList) {
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            //存储品牌列表
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
            //根据模板id查询规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());
            //存储规格列表
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);

        }
    }

    @Override
    public List<Map> findSpecList(Long id) {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        for (Map map : maps) {
            TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(optionExample);
            map.put("options", options);
        }
        return maps;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);

        //存入数据到缓存
        saveToRedis();
        return new PageResult(page.getTotal(), page.getResult());
    }

}
