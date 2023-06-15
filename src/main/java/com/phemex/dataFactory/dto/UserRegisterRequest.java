package com.phemex.dataFactory.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.dto.UserRegisterRequest
 * @Date: 2023年06月12日 10:59
 * @Description:
 */

@Data
public class UserRegisterRequest implements Serializable {
    private String emailPrefix;
    private String emailSuffix;
    private String password;
    private int numStart;
    private int numEnd;
    private List<Integer> numList;
    private int reqDelayMs;
}
