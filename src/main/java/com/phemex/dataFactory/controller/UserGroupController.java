package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.request.base.UserGroup;
import com.phemex.dataFactory.service.UserGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api
@RequestMapping("group")
@RestController
public class UserGroupController {
    @Resource
    private UserGroupService userGroupService;

    @ApiOperation(value = "用户组列表查询", notes = "", httpMethod = "GET")
    @GetMapping("/list")
    public List<UserGroup> getUserGroupList() {
        return userGroupService.getUserGroupList();
    }

}
