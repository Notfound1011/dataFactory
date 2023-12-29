package com.phemex.dataFactory.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.response.LdapUserDTO
 * @Date: 2022年05月12日 18:59
 * @Description:
 */
@Data
public class LdapUserResp implements Serializable {
    private String id;

    private String user;

    private String group;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
