package com.phemex.dataFactory.service;

import com.phemex.dataFactory.common.utils.LogUtil;
import com.phemex.dataFactory.config.LdapProperties;
import com.phemex.dataFactory.dto.LdapUserDTO;
import com.phemex.dataFactory.mapper.LdapMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.LdapService
 * @Date: 2022年05月12日 15:07
 * @Description:
 */
@Service
public class LdapService {
    @Resource
    private LdapMapper ldapMapper;

    LdapProperties ldapProperties = new LdapProperties();
    public Object getUsers() {
        String adminName = ldapProperties.getAdminName();
        String password = ldapProperties.getPassword();
        String ldapUrl = ldapProperties.getLdapUrl();
        String searchBase = ldapProperties.getSearchBase();
        LdapContext ctx = null;
        //1. 设置初始LDAP上下文的属性，并初始化LDAP上下文
        Hashtable<String, String> env = DirContext(adminName, password, ldapUrl);
        return getUsers(ctx, searchBase, env);
    }

//    public void insertUsers() {
//        String user = "yuyu.shi";
//        List<String> newGroupList = new ArrayList();
//        newGroupList.add("aaa");
//        newGroupList.add("bbb");
//        LdapUserDTO ldapUserDTO = new LdapUserDTO();
//        ldapUserDTO.setUser(user);
//        ldapUserDTO.setGroup(newGroupList.toString());
//        ldapUserDTO.setId(UUID.randomUUID().toString());
//        ldapUserDTO.setCreateTime(System.currentTimeMillis());
//        ldapUserDTO.setUpdateTime(System.currentTimeMillis());
//        ldapMapper.insert(ldapUserDTO);
//    }

    public List<LdapUserDTO> select(){
         return ldapMapper.select();
    }

    private Object getUsers(LdapContext ctx, String searchBase, Hashtable<String, String> env) {
        HashMap<String, List<String>> result = new HashMap<>();
        String[] groupFilter = ldapProperties.getGroupFilter();
        String[] returningAttributes = ldapProperties.getReturningAttributes();
        try {
            ctx = new InitialLdapContext(env, null);
            //2. 设置查询的属性
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(returningAttributes);
            //3. 通过上下文查询记录
            NamingEnumeration<SearchResult> results = ctx.search(searchBase, "(objectClass=user)", constraints);

            //4. 获取查询的内容
            while (results.hasMore()) {
                SearchResult sr = results.next();
                Attributes attr = sr.getAttributes();
                try {
                    String user = attr.get("sAMAccountName").toString().split(":")[1].trim();
                    String group = attr.get("memberOf").toString();
                    List<String> groupList = new ArrayList<>();
                    for (int i = 0; i < group.split(":|,").length; i++) {
                        String tmp = group.split(":|,")[i].trim();
                        if (tmp.startsWith("CN=")) {
                            groupList.add(tmp.split("CN=")[1]);
                        }
                    }

                    List<String> newGroupList = new ArrayList<>(groupList);
                    for (String s : groupFilter) {
                        newGroupList.remove(s);
                    }

                    LdapUserDTO ldapUserDTO = new LdapUserDTO();
                    ldapUserDTO.setUser(user);
                    ldapUserDTO.setGroup(newGroupList.toString());
                    ldapUserDTO.setId(UUID.randomUUID().toString());
                    ldapUserDTO.setCreateTime(System.currentTimeMillis());
                    ldapUserDTO.setUpdateTime(System.currentTimeMillis());
                    if (ldapMapper.countByUser(ldapUserDTO.getUser()) > 0) {
                        ldapMapper.updateByUser(ldapUserDTO);
                    } else {
                        ldapMapper.insert(ldapUserDTO);
                    }
                    result.put(user, newGroupList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //5. 关闭	上下文，释放连接
        finally {
            if (ctx != null) {
                try {
                    ctx.close();// 4.0 close Connection
                    LogUtil.info("Close Ldap Successful.");
                } catch (NamingException e) {
                    LogUtil.error("Exception in ldapClose(): ", e);
                }
            }
        }
        return result;
    }

    private Hashtable<String, String> DirContext(String adminName, String password, String ldapUrl) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");// Factory用于创建InitialContext对象
        env.put(Context.SECURITY_AUTHENTICATION, "Simple");
        env.put(Context.SECURITY_PRINCIPAL, adminName);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, ldapUrl);
        return env;
    }
}
