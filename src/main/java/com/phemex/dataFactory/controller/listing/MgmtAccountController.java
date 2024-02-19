package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.model.MgmtAccount;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.MgmtAccountService;
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
@RequestMapping("listing/mgmt-account")
@RestController
public class MgmtAccountController {
    @Resource
    private MgmtAccountService mgmtAccountService;

    @ApiOperation(value = "更新管理后台账号信息", notes = "", httpMethod = "POST")
    @PostMapping("/save")
    public ResultHolder updateUser(@RequestBody @Valid MgmtAccount mgmtAccount) {
        return mgmtAccountService.saveOrUpdateMgmtAccount(mgmtAccount);
    }

    @ApiOperation(value = "管理后台账号列表查询", notes = "", httpMethod = "GET")
    @GetMapping("/list")
    public List<MgmtAccount> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) Integer id) {
        return mgmtAccountService.getMgmtAccountList(username,owner,id);
    }
}
