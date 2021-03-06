package com.edu.nju.seckill.controller;

import com.edu.nju.seckill.common.CommonResult;
import com.edu.nju.seckill.domain.User;
import com.edu.nju.seckill.domain.dto.CurrentUser;
import com.edu.nju.seckill.domain.dto.FavoriteResult;
import com.edu.nju.seckill.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lql
 * @date 2020/1/11 20:25
 */
@RestController
@Api(tags = "收藏夹控制类")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation(value = "get参数解析器测试",notes = "testGet")
    @GetMapping("/favorite/get")
    public String getTest( User user){
        return user.toString();
    }

    @ApiOperation(value = "post参数解析器测试",notes = "testPost")
    @PostMapping("/favorite/post")
    public String postTest(User user,  @RequestParam String string){
        return user.getName()+string;
    }

    @ApiOperation(value = "添加收藏夹",notes = "addFavorite")
    @PostMapping("/favorite")
    public CommonResult<String> addFavorite(CurrentUser currentUser, @RequestParam long gid){
        User user=currentUser.getUser();
        if(favoriteService.addFavorite(user.getUid(),gid)) {
            return CommonResult.success(null, "添加成功");
        }else {
            return CommonResult.failed("添加失败");
        }
    }

    /**
    * @Description: 通过收藏夹表主键fid删除收藏记录
    * @Param: [fid]
    * @return: com.edu.nju.seckill.common.CommonResult<java.lang.String>
    * @Author: whn
    * @Date: 2020/1/29
    */
    @ApiOperation(value = "从收藏夹删除",notes = "addFavorite")
    @DeleteMapping("/favorite/{fid}")
    public CommonResult<String> deleteFavorite(@PathVariable int fid){
        if(favoriteService.deleteFavorite(fid)) {
            return CommonResult.success(null, "删除成功");
        }else {
            return CommonResult.failed("删除失败或无该记录");
        }
    }
    /**
    * @Description: 通过keyword模糊查询收藏的商品信息
    * @Param: [user, keyword]
    * @return: com.edu.nju.seckill.common.CommonResult<com.edu.nju.seckill.domain.dto.FavoriteResult>
    * @Author: whn
    * @Date: 2020/1/30
    */
    @ApiOperation(value="从收藏夹通过keyword搜索",notes="searchFavoriteByKeyword")
    @GetMapping(value = {"/favorite/list/{keyword}","/favorite/list"})
    public CommonResult<List<FavoriteResult>> searchFavoriteByKeyword(CurrentUser currentUser, @PathVariable(required = false) String keyword){
        User user=currentUser.getUser();
        if(null==keyword)
            keyword="";
        List<FavoriteResult> res=favoriteService.searchFavoriteByKeyword(user.getUid(),keyword);
        if(res.size()>0)
            return CommonResult.success(res);
        else
            return CommonResult.failed("无对应收藏商品");
    }
}
