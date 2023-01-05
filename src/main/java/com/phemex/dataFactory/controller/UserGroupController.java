package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.base.domain.User;
import com.phemex.dataFactory.base.domain.UserGroup;
import com.phemex.dataFactory.controller.request.UserRequest;
import com.phemex.dataFactory.service.UserGroupService;
import com.phemex.dataFactory.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.UserController
 * @Date: 2022年09月01日 11:52
 * @Description:
 */
@RequestMapping("group")
@RestController
public class UserGroupController {
    @Resource
    private UserGroupService userGroupService;

    @GetMapping("/list")
    public List<UserGroup> getUserGroupList() {
        return userGroupService.getUserGroupList();
    }

}
