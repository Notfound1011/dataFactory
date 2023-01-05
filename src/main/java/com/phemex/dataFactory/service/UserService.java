package com.phemex.dataFactory.service;

import com.phemex.dataFactory.base.domain.User;
import com.phemex.dataFactory.base.domain.UserExample;
import com.phemex.dataFactory.common.constants.UserStatus;
import com.phemex.dataFactory.common.exception.DFException;
import com.phemex.dataFactory.common.utils.ActionEncoder;
import com.phemex.dataFactory.mapper.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.UserService
 * @Date: 2022年09月01日 11:52
 * @Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    public User insert(User userRequest) {
        checkUserParam(userRequest);
        String id = userRequest.getId();
        User user = userMapper.selectByPrimaryKey(id);
        if (user != null) {
            DFException.throwException("用户id已存在");
        } else {
            createUser(userRequest);
        }
        return getUser(userRequest.getId());
    }

    public void createUser(User userRequest) {
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        // 默认1:启用状态
        user.setStatus(UserStatus.NORMAL);
        checkEmailIsExist(user.getEmail());
        userMapper.insertSelective(user);
    }

    public User getUser(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        User userDTO = new User();
        BeanUtils.copyProperties(user, userDTO);

        return user;
    }

    private void checkEmailIsExist(String email) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andEmailEqualTo(email);
        List<User> userList = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(userList)) {
            DFException.throwException("用户邮箱已存在");
        }
    }

    private void checkUserParam(User user) {
        if (StringUtils.isBlank(user.getId())) {
            DFException.throwException("用户ID不能为空");
        }

        if (StringUtils.isBlank(user.getName())) {
            DFException.throwException("用户名不能为空");
        }

        if (StringUtils.isBlank(user.getEmail())) {
            DFException.throwException("用户邮箱不能为空");
        }
    }

    public void updateUser(User user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andEmailEqualTo(user.getEmail());
            criteria.andIdNotEqualTo(user.getId());
            if (userMapper.countByExample(example) > 0) {
                DFException.throwException("用户邮箱已存在");
            }
        }
        user.setUpdateTime(System.currentTimeMillis());
        userMapper.updateByPrimaryKeySelective(user);
    }

    public List<User> getUserList() {
        UserExample example = new UserExample();
        example.setOrderByClause("update_time desc");
        log.info("查询用户列表成功");
        return userMapper.selectByExample(example);
    }

    public void deleteUser(String userId) {
        userMapper.deleteByPrimaryKey(userId);
    }
}
