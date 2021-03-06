package com.edu.nju.seckill.service;

import com.edu.nju.seckill.domain.SeckillGoods;
import com.edu.nju.seckill.domain.dto.SeckillGoodsList;

import java.util.List;

/**
 * @author lql
 * @date 2020/1/11 20:20
 */
public interface SeckillGoodsService {

     boolean insertSeckillGoods(SeckillGoods seckillGoods);

    SeckillGoods getSeckillBySgid(Long sgid);

    /***
     * 获取秒杀商品列表，开始和结束时间相同的商品放在一个list中
     * @return
     */
    List<SeckillGoodsList> getSeckillList();

    /***
     * 获取最近的秒杀商品列表
     * @return
     */
    SeckillGoodsList getLatestSeckillGoods();
}
