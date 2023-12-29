package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.Application;
import com.phemex.dataFactory.common.utils.LogUtil;
import com.phemex.dataFactory.response.LdapUserResp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.example.demo.LdapTest
 * @Date: 2022年05月12日 22:48
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
@EnableAutoConfiguration
public class LdapTest {
    //2、注入mapper接口
    @Autowired
    private LdapMapper ldapMapper;

    @Before
    public void before() {
        System.out.println("....................start....................");
    }

    //3、标注测试方法
    @Test
    public void testInsert() {
        //4、创建类
        LdapUserResp ldapUserResp = new LdapUserResp();
        List<String> newGroupList = new ArrayList();
        newGroupList.add("aaa");
        newGroupList.add("bbb");
        ldapUserResp.setId("1");
        ldapUserResp.setUser("yuyu");
        ldapUserResp.setGroup(newGroupList.toString());
        ldapUserResp.setCreateTime(System.currentTimeMillis());
        ldapUserResp.setUpdateTime(System.currentTimeMillis());
        //5、调用方法
        ldapMapper.insert(ldapUserResp);
    }

    @Test
    public void testUpdate() {
        //4、创建类
        LdapUserResp ldapUserResp = new LdapUserResp();
        List<String> newGroupList = new ArrayList();
        newGroupList.add("aaa");
        newGroupList.add("bbb");
//        ldapUserDTO.setId("1");
        ldapUserResp.setUser("yuyu.shi");
        ldapUserResp.setGroup(newGroupList.toString());
        ldapUserResp.setCreateTime(System.currentTimeMillis());
        ldapUserResp.setUpdateTime(System.currentTimeMillis());
        //5、调用方法
        ldapMapper.updateByUser(ldapUserResp);
    }

    @Test
    public void testGet() {
        //5、调用方法
        List<LdapUserResp> aa = ldapMapper.selectAll();
        System.out.println(aa);
        LogUtil.info(aa);
    }

    @Test
    public void testCount() {
        System.out.println("testCount");
        //5、调用方法
        int a = ldapMapper.countByUser("yuyu.shi");
        Assert.assertEquals(a, 1);
    }
}
