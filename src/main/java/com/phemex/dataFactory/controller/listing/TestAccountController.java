package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.model.TestAccount;
import com.phemex.dataFactory.service.listing.TestAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.listing.UserController
 * @Date: 2024年01月26日 14:53
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing/test-account")
@RestController
public class TestAccountController {
    @Resource
    private TestAccountService testAccountService;

    @ApiOperation(value = "更新测试账号信息", notes = "", httpMethod = "POST")
    @PostMapping("/save")
    public ResultHolder updateUser(@RequestBody @Valid TestAccount testAccount) {
        return testAccountService.saveOrUpdateTestAccount(testAccount);
    }

    @ApiOperation(value = "测试账号列表查询", notes = "", httpMethod = "GET")
    @GetMapping("/list")
    public List<TestAccount> getUserList(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) Integer id) {
        return testAccountService.getTestAccountList(email,owner,id);
    }
}
