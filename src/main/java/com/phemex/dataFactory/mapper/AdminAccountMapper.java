package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.model.AdminAccount;

import java.util.List;


/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.mapper.TestAccountMapper
 * @Date: 2024年01月26日 15:52
 * @Description:
 */
public interface AdminAccountMapper {
    void insertSelective(AdminAccount adminAccount);

    void updateByPrimaryKeySelective(AdminAccount adminAccount);

    AdminAccount selectByPrimaryKey(int id);

    AdminAccount selectByOwner(String owner, String type);

    List<AdminAccount> selectByKey(String username, String type, String owner, Integer id);
}
