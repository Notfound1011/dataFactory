package com.phemex.dataFactory.mapper;

import com.phemex.dataFactory.dto.LdapUserDTO;
import org.apache.ibatis.annotations.Mapper;
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
    int insert(LdapUserDTO ldapUserDTO);

    int updateByUser(LdapUserDTO ldapUserDTO);

    Integer countByUser(@Param("user") String user);

    List<LdapUserDTO> selectAll();
}
