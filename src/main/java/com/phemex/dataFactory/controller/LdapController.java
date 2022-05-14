package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.controller.request.ResultHolder;
import com.phemex.dataFactory.dto.LdapUserDTO;
import com.phemex.dataFactory.service.LdapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

@RequestMapping("ldap")
@RestController
public class LdapController {
    @Resource
    LdapService ldapService;

    @GetMapping("/insertUsers")
    public Object insertUsers() {
        return ResultHolder.success(ldapService.getUsers());
    }

    @GetMapping("/selectUsers")
    public List<LdapUserDTO> selectUsers() {
        return ldapService.select();
    }
}
