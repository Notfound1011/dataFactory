package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.base.domain.User;
import com.phemex.dataFactory.controller.request.UserRequest;
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
@RequestMapping("user")
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public User insertUser(@RequestBody UserRequest user) {
        return userService.insert(user);
    }

    @PostMapping("/update")
    public void updateUser(@RequestBody UserRequest user) {
        userService.updateUser(user);
    }

    @GetMapping("/list")
    public List<User> getUserList() {
        return userService.getUserList();
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
    }
}
