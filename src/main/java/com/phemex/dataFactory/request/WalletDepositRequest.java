package com.phemex.dataFactory.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description:
 */
@Getter
@Setter
public class WalletDepositRequest {
    private String host;
    private String path;
    private Map<String, Object> urlParam;
}