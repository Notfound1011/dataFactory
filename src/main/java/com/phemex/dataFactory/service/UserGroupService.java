package com.phemex.dataFactory.service;

import com.phemex.dataFactory.request.base.UserGroup;
import com.phemex.dataFactory.mapper.UserGroupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.UserGroupService
 * @Date: 2022年09月01日 11:52
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserGroupService {
    @Resource
    private UserGroupMapper userGroupMapper;

    public List<UserGroup> getUserGroupList() {
        return userGroupMapper.list();
    }
}
