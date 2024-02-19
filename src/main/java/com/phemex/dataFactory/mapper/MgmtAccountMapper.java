package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.model.MgmtAccount;

import java.util.List;


/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.mapper.TestAccountMapper
 * @Date: 2024年01月26日 15:52
 * @Description:
 */
public interface MgmtAccountMapper {
    void insertSelective(MgmtAccount mgmtAccount);
    void updateByPrimaryKeySelective(MgmtAccount mgmtAccount);
    MgmtAccount selectByPrimaryKey(int id);
    MgmtAccount selectByOwner(String owner);
    List<MgmtAccount> selectByKey(String username,String owner, Integer id);
}
