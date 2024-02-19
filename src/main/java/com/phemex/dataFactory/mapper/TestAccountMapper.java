package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.model.TestAccount;

import java.util.List;


/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.mapper.TestAccountMapper
 * @Date: 2024年01月26日 15:52
 * @Description:
 */
public interface TestAccountMapper {
    void insertSelective(TestAccount testAccount);
    void updateByPrimaryKeySelective(TestAccount testAccount);
    TestAccount selectByPrimaryKey(int id);
    TestAccount selectByEmailUid(String email, Integer uid);
    List<TestAccount> selectByKey(String email,String owner, Integer id);
}
