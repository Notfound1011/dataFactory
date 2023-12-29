package com.phemex.dataFactory.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.request.UserRegisterRequest
 * @Date: 2023年06月12日 10:59
 * @Description:
 */

@Data
public class UserRegisterRequest implements Serializable {
    private String emailPrefix;
    private String emailSuffix;
    private String password;
    private String referralCode;
    private int numStart;
    private int numEnd;
    private List<Integer> numList;
    private int reqDelayMs;
}
