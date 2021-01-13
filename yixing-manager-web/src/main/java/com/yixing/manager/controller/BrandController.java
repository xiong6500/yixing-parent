package com.yixing.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yixing.entity.PageResult;
import com.yixing.entity.Result;
import com.yixing.pojo.TbBrand;
import com.yixing.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){

        return brandService.findAll();
    }

    /**
     *
     * @param page  当前页数
     * @param rows  每页显示条数
     * @return
     */
    @RequestMapping("/search")
    public PageResult findPage(int page,int rows,@RequestBody TbBrand brand){

        return brandService.findPage(brand,page,rows);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }

    @RequestMapping("/findById")
    public TbBrand findById(long id){
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/remove")
    public Result remove(long[] ids){
        try {
            brandService.remove(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
