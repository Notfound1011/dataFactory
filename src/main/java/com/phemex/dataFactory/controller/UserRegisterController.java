package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.controller.request.UserRequest;
import com.phemex.dataFactory.dto.RegistrationInfo;
import com.phemex.dataFactory.dto.UserRegisterRequest;
import com.phemex.dataFactory.service.UserRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.UserRegisterController
 * @Date: 2023年06月12日 09:54
 * @Description:
 */
@Api
@RestController
@RequestMapping("user")
public class UserRegisterController {
    @ApiOperation(value = "批量注册", notes = "用户批量注册", httpMethod = "POST")
    @PostMapping("/register/batch")
    public List<RegistrationInfo> registerUsers(@RequestBody UserRegisterRequest userRegisterRequest) throws Exception {
        return UserRegisterService.registerUsers(userRegisterRequest);
    }
}
