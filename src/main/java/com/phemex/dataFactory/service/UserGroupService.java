package com.phemex.dataFactory.service;

import com.phemex.dataFactory.base.domain.User;
import com.phemex.dataFactory.base.domain.UserExample;
import com.phemex.dataFactory.base.domain.UserGroup;
import com.phemex.dataFactory.common.constants.UserStatus;
import com.phemex.dataFactory.common.exception.DFException;
import com.phemex.dataFactory.mapper.UserGroupMapper;
import com.phemex.dataFactory.mapper.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
