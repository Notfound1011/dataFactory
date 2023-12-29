package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.request.base.User;
import com.phemex.dataFactory.request.base.UserExample;

import java.util.List;

public interface UserMapper {
    long countByExample(UserExample example);

    int insert(User record);

    int insertSelective(User record);

    int deleteByPrimaryKey(String id);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);
}
