package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.common.utils.*;
import org.springframework.web.bind.annotation.*;
import com.phemex.dataFactory.dto.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description:
 */
@RestController
@RequestMapping("wallet")
public class WalletController {

    @PostMapping("/deposit")
    public String walletDeposit(@RequestBody WalletDepositRequest walletDepositRequest) {
        try {
            String baseUrl = walletDepositRequest.getHost() + walletDepositRequest.getPath();
            LogUtil.info("接口请求: " + baseUrl);
            return HttpClientUtil.put(baseUrl, walletDepositRequest.getUrlParam(), null, null);
        } catch (Exception ex) {
            LogUtil.error(ex);
            return null;
        }
    }
}
