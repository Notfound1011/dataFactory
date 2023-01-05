package com.phemex.dataFactory.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.base.domain.User
 * @Date: 2022年09月01日 11:58
 * @Description:
 */
@Data
public class UserGroup implements Serializable {
    private String id;

    private String name;

    private String content;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}
