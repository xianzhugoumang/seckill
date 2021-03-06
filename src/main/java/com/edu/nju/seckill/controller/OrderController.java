package com.edu.nju.seckill.controller;

import com.edu.nju.seckill.common.CommonResult;
import com.edu.nju.seckill.domain.Order;
import com.edu.nju.seckill.domain.User;
import com.edu.nju.seckill.domain.dto.CurrentUser;
import com.edu.nju.seckill.domain.dto.OrderParam;
import com.edu.nju.seckill.domain.dto.OrderSearchResult;
import com.edu.nju.seckill.service.OrderService;
import com.edu.nju.seckill.utils.OrderIdUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lql
 * @date 2020/1/11 20:25
 */
@RestController
@Api(tags = "订单控制类")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
    * @Description: 订单页-搜索订单项，根据keyword查询用户的订单，如果keyword为空，则显示全部订单
    * @Param: [currentUser, keyword]
    * @return: com.edu.nju.seckill.common.CommonResult<java.util.Map>
    * @Author: whn
    * @Date: 2020/2/7
    */
    @ApiOperation(value = "订单页-搜索订单项，根据keyword查询用户的订单，如果keyword为空，则显示全部订单")
    @GetMapping({"/order/list/{status}/{keyword}","/order/list/{status}"})
    public CommonResult<Map> searchOrder(CurrentUser currentUser,
                                         @PathVariable int status,
                                         @PathVariable(required = false) String keyword) {
        if (null == keyword) {
            keyword = "";
        }
        User user = currentUser.getUser();
        List<OrderSearchResult> res = orderService.searchOrder(user.getUid(),status, keyword);
        Map<String,List> map=new HashMap<>();
        map.put("ordItems",res);
        if (res.size() > 0) {
            return CommonResult.success(map, "操作成功");
        }
        else {
            return CommonResult.failed("无有效订单数据");
        }
    }

    /**
    * @Description:
    * @Param: [currentUser, orderParam]
    * @return: com.edu.nju.seckill.common.CommonResult<?>
    * @Author: lql
    * @Date: 2020/2/12
    */
    @ApiOperation("创建秒杀订单")
    @PostMapping("/order/create")
    public CommonResult<?> createSeckillOrder(CurrentUser currentUser, @RequestBody OrderParam orderParam){
        //生成订单id
        OrderIdUtils orderIdUtils=OrderIdUtils.getInstance();
        Long oid= orderIdUtils.nextId();
        if(oid!=null&&currentUser.getUser().getUid()!=null&&orderParam!=null){
            Order order=new Order(orderParam,currentUser.getUser().getUid(),oid);
            if( orderService.createOrder(order)){
                return CommonResult.success("订单创建成功！");
            }else {
                return CommonResult.failed("创建失败");
            }
        }else {
            return CommonResult.validateFailed();
        }

    }
    @ApiOperation("根据oid删除订单")
    @DeleteMapping("/order/{oid}")
    public CommonResult<?> deleteOrder(@PathVariable(name = "oid",required = true) long oid){

        if(orderService.deleteByOid(oid)){
            return CommonResult.success("删除成功！");
        }else {
            return CommonResult.validateFailed("该订单已被删除！");
        }
    }
    @ApiOperation("根据oid获取订单详情")
    @GetMapping("/order/info/{oid}")
    public CommonResult<?> getOrderInfo(@PathVariable(name = "oid") long oid){

        Order order=orderService.getOrderInfo(oid);
        if(order!=null){
            return CommonResult.success(order);
        }else{
            return CommonResult.failed("订单已被删除或订单不存在！");
        }
    }
}
