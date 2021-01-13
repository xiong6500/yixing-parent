package com.yixing.sellergoods.service;

import com.yixing.entity.PageResult;
import com.yixing.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    /**
     * 返回分页列表
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 添加数据
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 根据id查询品牌数据
     * @param id
     * @return
     */
    public TbBrand findById(long id);

    /**
     * 修改品牌数据
     * @param tbBrand
     */
    public void update(TbBrand tbBrand);

    /**
     * 根据id删除数据
     * @param ids
     */
    public void remove(long[] ids);

    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    List<Map> selectOptionList();
}
