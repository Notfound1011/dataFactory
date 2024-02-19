package com.phemex.dataFactory.controller.user;

import com.phemex.dataFactory.request.base.User;
import com.phemex.dataFactory.request.UserRequest;
import com.phemex.dataFactory.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.user.UserController
 * @Date: 2022年09月01日 11:52
 * @Description:
 */
@Api(tags = "User")
@RequestMapping("user")
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "添加用户", notes = "", httpMethod = "POST")
    @PostMapping("/add")
    public User insertUser(@RequestBody UserRequest user) {
        return userService.insert(user);
    }

    @ApiOperation(value = "更新用户信息", notes = "", httpMethod = "POST")
    @PostMapping("/update")
    public void updateUser(@RequestBody UserRequest user) {
        userService.updateUser(user);
    }

    @ApiOperation(value = "用户列表查询", notes = "", httpMethod = "GET")
    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    @ApiOperation(value = "删除用户", notes = "", httpMethod = "DELETE")
    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
    }
}
