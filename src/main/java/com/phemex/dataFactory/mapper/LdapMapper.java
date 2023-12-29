package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.response.LdapUserResp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.mapper.LdapMapper
 * @Date: 2022年05月12日 19:44
 * @Description:
 */
@Repository
public interface LdapMapper {
    int insert(LdapUserResp ldapUserResp);

    int updateByUser(LdapUserResp ldapUserResp);

    Integer countByUser(@Param("user") String user);

    List<LdapUserResp> selectAll();
}
