package com.phemex.dataFactory.controller.user;

import com.phemex.dataFactory.response.RegistrationResp;
import com.phemex.dataFactory.request.UserRegisterRequest;
import com.phemex.dataFactory.service.user.UserRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.user.UserRegisterController
 * @Date: 2023年06月12日 09:54
 * @Description:
 */
@Api(tags = "User")
@RestController
@RequestMapping("user")
public class UserRegisterController {
    @ApiOperation(value = "用户批量注册", notes = "", httpMethod = "POST")
    @PostMapping("/register/batch")
    public List<RegistrationResp> registerUsers(@RequestBody UserRegisterRequest userRegisterRequest) throws Exception {
        return UserRegisterService.registerUsers(userRegisterRequest);
    }
}
