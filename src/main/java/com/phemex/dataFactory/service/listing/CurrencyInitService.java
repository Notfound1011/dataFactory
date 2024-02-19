package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.base.PhemexManageApi;
import com.phemex.dataFactory.request.listing.CurrencyInfo;
import com.phemex.dataFactory.request.listing.CurrencyInitRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.CurrencyInitService
 * @Date: 2024年01月22日 16:59
 * @Description:
 */
@Service
public class CurrencyInitService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyInitService.class);
    private static Map<String, String> phemexManageHostMap;

    public CurrencyInitService(Map<String, String> phemexManageHostMap) {
        CurrencyInitService.phemexManageHostMap = phemexManageHostMap;
    }

    public ResultHolder run(CurrencyInitRequest currencyInitRequest) {
        String env = currencyInitRequest.getEnv().toLowerCase();

        String host = phemexManageHostMap.get(env);
        String path = "/phemex-admin/phemex-common-service/admin/coin/basic";
        String url = host + path;
        if (currencyInitRequest.getUserName() == null || currencyInitRequest.getUserName().isEmpty() ||
                currencyInitRequest.getPassword() == null || currencyInitRequest.getPassword().isEmpty()) {
            return ResultHolder.error("初始化失败，请先到账号设置 设置管理后台账号！！！");
        }
        Map<String, String> header = getManageToken(host, currencyInitRequest);

        List<String> successCurrencies = new ArrayList<>();
        List<String> failedCurrencies = new ArrayList<>();
        boolean allSuccess = true;

        for (CurrencyInfo currencyInfo : currencyInitRequest.getCurrencies()) {
            if (currencyInfo.getTagOrMemo() == "memo") {
                currencyInfo.setAddrExtra(1);
            } else if (currencyInfo.getTagOrMemo() == "tag") {
                currencyInfo.setAddrExtra(2);
            } else {
                currencyInfo.setAddrExtra(0);
            }

            Map<String, Object> currencyMap = currencyInfo.toMap();

            String response;
            try {
                response = HttpClientUtil.jsonPost(url, currencyMap, header);
                successCurrencies.add(currencyInfo.getCurrency());
            } catch (Exception e) {
                log.error("Failed to initialize currency: " + currencyInfo.getCurrency(), e);
                failedCurrencies.add(currencyInfo.getCurrency());
                allSuccess = false;
                continue; // Skip to the next currency
            }

            log.info("Response for initializing currency {}: {}", currencyInfo.getCurrency(), response);
            JSONObject jsonObject = JSONObject.parseObject(response);

            if (jsonObject.getString("code").equals("401")) {
                return ResultHolder.error("初始化失败，管理后台账号账号或密码不正确！！！");
            } else if (!jsonObject.getString("code").equals("0")) {
                log.error("Currency initialization failed for {}: {}", currencyInfo.getCurrency(), jsonObject.toString());
                failedCurrencies.add(currencyInfo.getCurrency());
                allSuccess = false;
            }
        }

        if (allSuccess) {
            // 所有货币初始化成功
            return ResultHolder.success("All currencies initialized successfully", String.join(", ", successCurrencies));
        } else {
            // 部分货币初始化失败
            return ResultHolder.error("Currency initialization failed for: " + String.join(", ", failedCurrencies));
        }
    }


    public static Map<String, String> getManageToken(String host, PhemexManageApi phemexManageApi) {
        Map<String, String> tokenMap = new HashMap<>();
        String url = host + "/phemex-admin/phemex-entitlement/login";

        CloseableHttpResponse response;
        try {

            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-type", "application/json");

            Map<String, Object> json = new HashMap<>();
            json.put("email", phemexManageApi.getUserName());
            json.put("username", phemexManageApi.getUserName());
            json.put("password", phemexManageApi.getPassword());
            json.put("google2fa", "1");

            response = HttpClientUtil.httpPost(url, json, header);

            if (response.getStatusLine().getStatusCode() == 200) {
                tokenMap.put("phemex-admin-token", response.getFirstHeader("phemex-admin-token").getValue());
                log.info(tokenMap.toString());
                return tokenMap;
            } else {
                log.error("ManageConfig获取账号信息失败!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
