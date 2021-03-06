package com.edu.nju.seckill.controller;

import com.edu.nju.seckill.common.CommonResult;
import com.edu.nju.seckill.domain.User;
import com.edu.nju.seckill.domain.dto.*;
import com.edu.nju.seckill.service.GoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lql
 * @date 2020/1/11 20:25
 */
@RestController
@Api(tags = "普通商品控制类")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 查找获取重点商品轮播图列表
     * @return
     */
    @ApiOperation(value = "获取重点商品轮播图列表",notes = "返回轮播列表list")
    @GetMapping("/goods/carouselItems")
    public CommonResult<List<CarouselItems>> getHotProductCarousel(){
        return CommonResult.success(goodsService.getHotProductCarousel(),"操作成功");
    }

    @ApiOperation(value="获取普通商品列表按type分类 以orderby排序 以keyword搜索",notes="返回商品表")
    @GetMapping(value = {"/goods/{type}/list/{orderby}/{pageNum}/{pageSize}/{keyword}",
                        "/goods/{type}/list/{orderby}/{pageNum}/{pageSize}"})
    public CommonResult<List<GoodsListResult>> getGoodsList(
            @PathVariable String type,
            @PathVariable(required = false) String keyword,
            @PathVariable Integer pageNum,
            @PathVariable Integer pageSize,
            @PathVariable String orderby){
        Page page = PageHelper.startPage(pageNum,pageSize,true);
        Object obj=page.getTotal();
        if(null==keyword)
            keyword="";
        //前端默认值为default
        if("default".equals(orderby))
            orderby="gid";
        //前端传输salescount 但数据库中为count字段
        else if(orderby.equals("salecount"))
            orderby="count";
        List<GoodsListResult> res=goodsService.getGoodsList(type,orderby,keyword);
        if(res.size()>0)
            return CommonResult.success(res,"操作成功");
        else
            return CommonResult.failed("无数据");
    }

    @ApiOperation(value = "通过gid获取显示商品详情",notes = "返回轮播列表list")
    @GetMapping("/goods/show/{gid}")
    public CommonResult<List<GoodsDetailResult>> getGoodDetail(@PathVariable long gid){
        List<GoodsDetailResult> res=goodsService.getGoodDetail(gid);
        if(res.size()>0)
            return CommonResult.success(res,"操作成功");
        else{
            return CommonResult.failed("无此商品");
        }
    }

    @ApiOperation(value = "商品搜索-获取商城首页商品列表。参数（可选）：keyword")
    @GetMapping({"/goods/list/{keyword}","/goods/list"})
    public CommonResult<Map> searchGoodForIndex(
            @PathVariable(required = false) String keyword){
        Map<String,List<GoodsSearchResult>> map=new HashMap<>();
        if(null==keyword)
            keyword="";
        List<GoodsSearchResult> res=goodsService.searchGoodsForIndex(keyword);
        map.put("list",res);
        if(res.size()>0)
            return CommonResult.success(map,"操作成功");
        else
            return CommonResult.failed("无数据");
    }

    @ApiOperation(value = "商品搜索-获取首页商品搜索框的搜索提示")
    @GetMapping(value = {"/goods/tips/{keyword}","/goods/tips"})
    public CommonResult<Map> getGoodsIndexTips(@PathVariable(required = false)String keyword){
        if(null==keyword)
            return CommonResult.success(null,"操作成功数据为空");
        List<String> list=goodsService.getGoodsIndexTips(keyword);
        Map<String,List> map=new HashMap<>();
        map.put("results",list);
        return CommonResult.success(map,"操作成功");
    }
}
