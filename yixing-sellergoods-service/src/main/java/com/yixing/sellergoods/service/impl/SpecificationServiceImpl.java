package com.yixing.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yixing.entity.PageResult;
import com.yixing.group.Specification;
import com.yixing.mapper.TbSpecificationMapper;
import com.yixing.mapper.TbSpecificationOptionMapper;
import com.yixing.pojo.TbSpecification;
import com.yixing.pojo.TbSpecificationExample;
import com.yixing.pojo.TbSpecificationExample.Criteria;
import com.yixing.pojo.TbSpecificationOption;
import com.yixing.pojo.TbSpecificationOptionExample;
import com.yixing.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		TbSpecification specification1 = specification.getSpecification();
		specificationMapper.insert(specification1);
		for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
			specificationOption.setSpecId(specification1.getId());
			tbSpecificationOptionMapper.insert(specificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		TbSpecification specification1 = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(specification1);
		TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
		criteria.andSpecIdEqualTo(specification1.getId());
		//删除所有的规格项,然后添加
		tbSpecificationOptionMapper.deleteByExample(optionExample);
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			option.setSpecId(specification1.getId());//设置外键id
			tbSpecificationOptionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
		criteria.andSpecIdEqualTo(id);//根据id查询规格
		List<TbSpecificationOption> options = tbSpecificationOptionMapper.selectByExample(optionExample);
		return new Specification(tbSpecification,options);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample optionExample = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = optionExample.createCriteria();
			criteria.andSpecIdEqualTo(id);
			tbSpecificationOptionMapper.deleteByExample(optionExample);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
