package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.common.utils.*;
import com.phemex.dataFactory.request.WalletDepositRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Date: 2022年04月17日 19:30
 * @Description:
 */
@Api(tags = "Wallet")
@RestController
@RequestMapping("wallet")
public class WalletController {

    @ApiOperation(value = "用户充值", notes = "用户充值", httpMethod = "POST")
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
