package com.edu.nju.seckill.service.impl;

import com.edu.nju.seckill.dao.AddressMapper;
import com.edu.nju.seckill.domain.dto.AddressOperationParam;
import com.edu.nju.seckill.domain.dto.GetAddressResult;
import com.edu.nju.seckill.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lql
 * @date 2020/1/11 20:20
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * @Description: 新增收货地址
     * @Param: [uid, param]
     * @return: boolean
     * @Author: whn
     * @Date: 2020/2/3
     */
    @Override
    public boolean addAddress(Long uid, String postcode, String address, String receiver_name, String receiver_phone) {
        int res = addressMapper.addAddress(uid, postcode, address,
                receiver_name, receiver_phone);
        if (res > 0)
            return true;
        else
            return false;

    }

    /**
     * @Description: 获取已添加的收藏地址
     * @Param: [uid]
     * @return: java.util.List<com.edu.nju.seckill.domain.dto.GetAddressResult>
     * @Author: whn
     * @Date: 2020/2/3
     */
    @Override
    public List<GetAddressResult> getAddress(Long uid) {
        return addressMapper.getAddress(uid);
    }

    /**
     * @Description: 修改收货地址
     * @Param: [aid, uid, postcode, address, receiver_name, receiver_phone]
     * @return: boolean
     * @Author: whn
     * @Date: 2020/2/3
     */
    @Override
    public boolean updateAddress(Integer aid, Long uid, String postcode, String address, String receiver_name, String receiver_phone) {
        int res = addressMapper.updateAddress(aid, uid, postcode, address, receiver_name, receiver_phone);
        if (res > 0)
            return true;
        else
            return false;
    }

    /**
     * @Description: 删除收货地址
     * @Param: [aid, uid]
     * @return: boolean
     * @Author: whn
     * @Date: 2020/2/3
     */
    @Override
    public boolean deleteAddress(Integer aid, Long uid) {
        int res = addressMapper.deleteAddress(aid, uid);
        if (res > 0)
            return true;
        else
            return false;
    }

    /**
     * @Description: 修改默认收货地址
     * @Param: [uid, aid]
     * @return: boolean
     * @Author: whn
     * @Date: 2020/2/7
     */
    @Override
    public boolean updateDefaultAddress(Long uid, Integer aid) {
        //先判断该uid是否持有该aid
        if (addressMapper.isBelongToCurrentUser(uid, aid) == 1)
            if (addressMapper.updateDefaultAddress(uid, aid) == 1)
                return true;
        return false;
    }
}
