package com.phemex.dataFactory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.dto.LdapUserDTO
 * @Date: 2022年05月12日 18:59
 * @Description:
 */
@Data
public class LdapUserDTO implements Serializable {
    private String id;

    private String user;

    private String group;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
