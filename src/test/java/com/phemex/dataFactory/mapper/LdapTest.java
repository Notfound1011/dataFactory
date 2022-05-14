package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.Application;
import com.phemex.dataFactory.common.utils.LogUtil;
import com.phemex.dataFactory.dto.LdapUserDTO;
import com.phemex.dataFactory.mapper.LdapMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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
        LdapUserDTO ldapUserDTO = new LdapUserDTO();
        List<String> newGroupList = new ArrayList();
        newGroupList.add("aaa");
        newGroupList.add("bbb");
        ldapUserDTO.setId("1");
        ldapUserDTO.setUser("yuyu");
        ldapUserDTO.setGroup(newGroupList.toString());
        ldapUserDTO.setCreateTime(System.currentTimeMillis());
        ldapUserDTO.setUpdateTime(System.currentTimeMillis());
        //5、调用方法
        ldapMapper.insert(ldapUserDTO);
    }

    @Test
    public void testUpdate() {
        //4、创建类
        LdapUserDTO ldapUserDTO = new LdapUserDTO();
        List<String> newGroupList = new ArrayList();
        newGroupList.add("aaa");
        newGroupList.add("bbb");
//        ldapUserDTO.setId("1");
        ldapUserDTO.setUser("yuyu.shi");
        ldapUserDTO.setGroup(newGroupList.toString());
        ldapUserDTO.setCreateTime(System.currentTimeMillis());
        ldapUserDTO.setUpdateTime(System.currentTimeMillis());
        //5、调用方法
        ldapMapper.updateByUser(ldapUserDTO);
    }

    @Test
    public void testGet() {
        //5、调用方法
        List<LdapUserDTO> aa = ldapMapper.select();
        LogUtil.info(aa);
    }

    @Test
    public void testCount() {
        //5、调用方法
        ldapMapper.countByUser("yuyu.shi");

        LogUtil.info("====================");
        LogUtil.info(ldapMapper.countByUser("yuyu.shi"));
    }

}
