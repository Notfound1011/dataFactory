package com.phemex.dataFactory.model;

import lombok.Data;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.model.Base
 * @Date: 2024年01月26日 19:56
 * @Description:
 */
@Data
public class Base {
    private int id;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}
