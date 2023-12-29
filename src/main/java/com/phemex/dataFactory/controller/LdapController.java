package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.response.LdapUserResp;
import com.phemex.dataFactory.service.LdapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.LdapGroupMembersSearch
 * @Date: 2022年05月11日 11:52
 * @Description:
 */
@Api
@RequestMapping("ldap")
@RestController
public class LdapController {
    @Resource
    LdapService ldapService;

    @ApiOperation(value = "更新ldap用户数据", notes = "更新ldap用户数据", httpMethod = "GET")
    @GetMapping("/insertUsers")
    public Object insertUsers(@RequestParam(name = "debug", required = false, defaultValue = "false") boolean debug) {
        return ResultHolder.success(ldapService.getUsers(debug));
    }

    @ApiOperation(value = "查询ldap用户数据", notes = "查询ldap用户数据", httpMethod = "GET")
    @GetMapping("/getUsers")
    public List<LdapUserResp> getUsers() {
        return ldapService.selectAll();
    }
}
