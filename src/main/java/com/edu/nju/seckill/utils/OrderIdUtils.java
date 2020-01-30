package com.edu.nju.seckill.utils;

import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;

/**
 *  利用雪花算法，获取全局id
 *  @author lql
 * @date 2020/1/30 16:26
 */
public class OrderIdUtils {
    /***
     * 这个就是代表了机器id
     */
    private long workerId;
    /***
     * // 这个就是代表了机房id
     */
    private long datacenterId;
    /***
     * // 这个就是代表了一毫秒内生成的多个id的最新序号
     */
    private long sequence=0L;

    /***
     * 构造函数设为私有，外部无法使用new方法创建对象
     */
    private OrderIdUtils() {

        this.workerId=1;
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            this.datacenterId=(long) sums%32;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /***
     * 开始的时间戳
     */
    private long twepoch = 1288834974657L;
    /***
     * workerId为5位
     */
    private long workerIdBits = 5L;
    /***
     *atacenterId为5位
     */
    private long datacenterIdBits = 5L;


    /***
     *  这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
     */
//    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /***
     *   这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
     */
//    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private long sequenceBits = 12L;
    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long lastTimestamp = -1L;

    public long getWorkerId(){
        return workerId;
    }
    public long getDatacenterId() {
        return datacenterId;
    }
    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    /***
     *  这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
     * @return 返回long类型id
     */
    public synchronized long nextId() {
        // 这儿就是获取当前时间戳，单位是毫秒
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            System.err.printf(
                    "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
        // 这个时候就得把seqence序号给递增1，最多就是4096
        if (lastTimestamp == timestamp) {

            // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
            //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }

        } else {
            sequence = 0;
        }
        // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
        lastTimestamp = timestamp;
        // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
        // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
        // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) | sequence;
    }
    private long tilNextMillis(long lastTimestamp) {

        long timestamp = timeGen();

        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    private long timeGen(){
        return System.currentTimeMillis();
    }

    /***
     * 调用私有构造函数
     */
    private static final OrderIdUtils INSTANCE=new OrderIdUtils();

    //获取单一实例
    public static OrderIdUtils getInstance(){
        return INSTANCE;
    }
}