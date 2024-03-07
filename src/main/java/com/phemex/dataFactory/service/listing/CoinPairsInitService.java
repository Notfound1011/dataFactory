package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.base.PhemexManageApi;
import com.phemex.dataFactory.request.listing.CoinPairsInitRequest;
import org.jetbrains.annotations.Nullable;
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
 * @Package: com.phemex.dataFactory.service.listing.NacosSync
 * @Date: 2024年01月17日 17:17
 * @Description:
 */
@Service
public class CoinPairsInitService {
    private static final Logger log = LoggerFactory.getLogger(CoinPairsInitService.class);
    private final Map<String, String> phemexManageHostMap;
    private static final String accessToken = "NjU0Njk4NzExOTM3OuMgBPBWch9bZRnnuy6NqsCXl3Gq";
    private static final String urlString = "http://bitbucket.cmexpro.com/rest/api/1.0/projects/DEVOPS/repos/config-nacos/raw/golden-copy/DEFAULT_GROUP%40common%40product-cfg.json?at=refs%2Fheads%2FFat";
    private final TokenService tokenService;

    public CoinPairsInitService(Map<String, String> phemexManageHostMap, TokenService tokenService) {
        this.phemexManageHostMap = phemexManageHostMap;
        this.tokenService = tokenService;
    }

    public ResultHolder coinPairsInit(CoinPairsInitRequest coinPairsInitRequest) {

        String fileContent = getFileContent(urlString, accessToken);

        Map<String, Map<String, String>> codesMap;
        codesMap = getCodesTypesFromJson(fileContent, coinPairsInitRequest.getSymbolList());

        return coinPairsInit(coinPairsInitRequest.getEnv(), coinPairsInitRequest.getMgmtAccount(), codesMap);
    }

    /**
     * @Description: 获取bitbucket文件内容
     * @Date: 2024/2/27
     * @Param url: bitbucket的链接
     * @Param accessToken: bitbucket的token
     **/
    private static String getFileContent(String url, String accessToken) {

        String result;
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", "Bearer " + accessToken);
        try {
            result = HttpClientUtil.get(url, header);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    // 获取Symbol对应的code
    private static Map<String, Map<String, String>> getCodesTypesFromJson(String jsonString, List<String> symbolList) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray products = jsonObject.getJSONArray("products");
        Map<String, Map<String, String>> codesAndTypesMap = new HashMap<>();

        for (int i = 0; i < products.size(); i++) {
            JSONObject product = products.getJSONObject(i);
            String symbol = product.getString("symbol");
            if (symbolList.contains(symbol)) {
                Map<String, String> codeAndType = new HashMap<>();
                codeAndType.put("code", product.getString("code"));
                codeAndType.put("type", product.getString("type"));
                codesAndTypesMap.put(symbol, codeAndType);
            }
        }
        return codesAndTypesMap;
    }

    // 登录管理后台，初始化币对
    public ResultHolder coinPairsInit(String env, PhemexManageApi phemexManageApi, Map<String, Map<String, String>> codesAndTypesMap) {
        if (env == null || env.isEmpty()) {
            env = "fat2";
        }
        String host = phemexManageHostMap.get(env);

        if (phemexManageApi.getUserName() == null || phemexManageApi.getUserName().isEmpty() ||
                phemexManageApi.getPassword() == null || phemexManageApi.getPassword().isEmpty()) {
            return ResultHolder.error("初始化失败，请先到账号设置 设置管理后台账号！！！");
        }
        Map<String, String> header = tokenService.getManageToken(host, phemexManageApi);

        List<String> successCurrencies = new ArrayList<>();
        List<String> failedCurrencies = new ArrayList<>();
        boolean allSuccess = true;

        // 遍历Map, 数据格式为{ETHUSDT={code=41641, type=PerpetualV2}}
        for (String key : codesAndTypesMap.keySet()) {
            Map<String, String> value = codesAndTypesMap.get(key);
            String type = value.get("type");
            String code = value.get("code");

            long timestamp = System.currentTimeMillis();
            String path = null;
            String jsonString = null;
            if (type.equals("PerpetualV2")) {
                path = "/phemex-admin/phemex-common-service/admin/coin-pairs/contract/edit";
                jsonString = String.format(
                        "{\"displayTime\":%d,\"listTime\":%d,\"alertTime\":0,\"deListTime\":0,\"hideTime\":0,\"internalDisplayTime\":%d,\"internalListTime\":%d,\"internalAlertTime\":0,\"internalDeListTime\":0,\"internalHideTime\":0,\"pairCode\":\"%s\",\"type\":\"PerpetualV2\",\"settleCurrency\":\"USDT\"}",
                        timestamp, timestamp, timestamp, timestamp, code);
            } else if (type.equals("Spot")) {
                path = "/phemex-admin/phemex-common-service/admin/coin-pairs/spot/edit";
                jsonString = String.format(
                        "{\"displayTime\":%d,\"listTime\":%d,\"alertTime\":0,\"deListTime\":0,\"hideTime\":0,\"internalDisplayTime\":%d,\"internalListTime\":%d,\"internalAlertTime\":0,\"internalDeListTime\":0,\"internalHideTime\":0,\"stStatus\":0,\"pairCode\":\"%s\"}",
                        timestamp, timestamp, timestamp, timestamp, code);
            }

            String url = host + path;

            String response;
            try {
                response = HttpClientUtil.jsonPost(url, jsonString, header);
                successCurrencies.add(key);
            } catch (Exception e) {
                log.error("Failed to initialize coin pair: " + key, e);
                failedCurrencies.add(key);
                allSuccess = false;
                continue; // Skip to the next currency
            }

            log.info("Response for initializing coin pair {}: {}", key, response);
            JSONObject jsonObject = JSONObject.parseObject(response);

            if (jsonObject.getString("code").equals("401")) {
                return ResultHolder.error("初始化失败，管理后台账号账号或密码不正确！！！");
            } else if (!jsonObject.getString("code").equals("0")) {
                log.error("coin pair initialization failed for {}: {}", key, jsonObject);
                failedCurrencies.add(key);
                allSuccess = false;
            }
        }

        ResultHolder updateToGateway = updateToGateway(codesAndTypesMap, host, header);
        if (updateToGateway != null) return updateToGateway;

        if (allSuccess) {
            // 所有币对初始化成功
            return ResultHolder.success("All coin pairs initialized successfully", String.join(", ", successCurrencies));
        } else {
            // 部分币对初始化失败
            return ResultHolder.error("coin pairs initialization failed for: " + String.join(", ", failedCurrencies));
        }
    }

    @Nullable
    private static ResultHolder updateToGateway(Map<String, Map<String, String>> codesAndTypesMap, String host, Map<String, String> header) {
        boolean containsPerpetualV2 = false;
        boolean containsSpot = false;
        for (Map<String, String> innerMap : codesAndTypesMap.values()) {
            if ("PerpetualV2".equals(innerMap.get("type"))) {
                containsPerpetualV2 = true;
            }
            if ("Spot".equals(innerMap.get("type"))) {
                containsSpot = true;
            }
            // 如果两者都找到了，就可以停止搜索
            if (containsPerpetualV2 && containsSpot) {
                break;
            }
        }

        // 更新币对配置后, Update to gateway
        String path = "/phemex-admin/phemex-common-service/admin/coin-pairs/change-notice";
        String url = host + path;
        if (containsPerpetualV2) {
            String jsonString = "{\"pairsType\":\"CONTRACT\"}";
            try {
                String response = HttpClientUtil.jsonPost(url, jsonString, header);
                log.info("CONTRACT Update to gateway success" + response);
            } catch (Exception e) {
                return ResultHolder.error("CONTRACT Update to gateway failed");
            }
        }
        if (containsSpot) {
            String jsonString = "{\"pairsType\":\"SPOT\"}";
            try {
                String response = HttpClientUtil.jsonPost(url, jsonString, header);
                log.info("SPOT Update to gateway success" + response);
            } catch (Exception e) {
                return ResultHolder.error("SPOT Update to gateway failed");
            }
        }
        return null;
    }
}
