package com.phemex.dataFactory.request.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.base.User
 * @Date: 2022年09月01日 11:58
 * @Description:
 */
@Data
public class User implements Serializable {
    private String id;

    private String name;

    private String email;

    private String role;

    private String group;

    private String status;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
