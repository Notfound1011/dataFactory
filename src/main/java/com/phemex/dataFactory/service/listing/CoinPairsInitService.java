package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.model.AdminAccount;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.base.PhemexAdminApi;
import com.phemex.dataFactory.request.listing.CoinPairsInitRequest;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
    private final OpsysService opsysService;
    private final AdminAccountService adminAccountService;

    public CoinPairsInitService(Map<String, String> phemexManageHostMap, TokenService tokenService, OpsysService opsysService, AdminAccountService adminAccountService) {
        this.phemexManageHostMap = phemexManageHostMap;
        this.tokenService = tokenService;
        this.opsysService = opsysService;
        this.adminAccountService = adminAccountService;
    }

    public ResultHolder coinPairsInit(CoinPairsInitRequest coinPairsInitRequest) {

        String fileContent = getFileContent(urlString, accessToken);

        Map<String, Map<String, String>> codesMap;
        codesMap = getCodesTypesFromJson(fileContent, coinPairsInitRequest.getSymbolList());

        return coinPairsInit(coinPairsInitRequest.getEnv(), coinPairsInitRequest.getMgmtAccount(), coinPairsInitRequest.getLdapAccount(), codesMap);
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
    public ResultHolder coinPairsInit(String env, PhemexAdminApi mgmtAccount, PhemexAdminApi ldapAccount, Map<String, Map<String, String>> codesAndTypesMap) {
        if (env == null || env.isEmpty()) {
            env = "fat2";
        }
        String host = phemexManageHostMap.get(env);
        AdminAccount adminAccount = adminAccountService.getAdminAccount(mgmtAccount.getOwner(), mgmtAccount.getAccountType());

        if (adminAccount.getUsername() == null || adminAccount.getUsername().isEmpty() ||
                adminAccount.getPassword() == null || adminAccount.getPassword().isEmpty()) {
            return ResultHolder.error("初始化失败，请先到账号设置 设置管理后台账号！！！");
        }
        Map<String, String> header = tokenService.getManageToken(host, mgmtAccount);

        List<String> successCoinPairs = new ArrayList<>();
        List<String> failedCoinPairs = new ArrayList<>();
        boolean allSuccess = true;

        ExecutorService executor = Executors.newFixedThreadPool(codesAndTypesMap.size());
        List<Future<ResultHolder>> futures = new ArrayList<>();

        for (String key : codesAndTypesMap.keySet()) {
            futures.add(executor.submit(() -> {
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
                try {
                    String response = HttpClientUtil.jsonPost(url, jsonString, header);
                    JSONObject.parseObject(response);

                    log.info("Response for initializing coin pair {}: {}", key, response);
                    return ResultHolder.success("Coin pair initialized successfully: " + key);
                } catch (Exception e) {
                    log.error("Failed to initialize coin pair: " + key, e);
                    return ResultHolder.error("Failed to initialize coin pair: " + key);
                }
            }));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            return ResultHolder.error("初始化失败，线程被中断。");
        }

        for (Future<ResultHolder> future : futures) {
            try {
                ResultHolder result = future.get();
                if (!result.isSuccess()) {
                    allSuccess = false;
                    failedCoinPairs.add(result.getMessage());
                } else {
                    successCoinPairs.add((String) result.getData());
                }
            } catch (InterruptedException | ExecutionException e) {
                allSuccess = false;
                log.error("Exception occurred during coin pair initialization.", e);
            }
        }

        ResultHolder updateToGateway = updateToGateway(codesAndTypesMap, host, header);
        if (updateToGateway != null) return updateToGateway;

        ResultHolder refreshCDN = opsysService.refreshCDN(ldapAccount);
        if (refreshCDN != null) return refreshCDN;


        if (allSuccess) {
            String successMessage = "All coin pairs initialized successfully";
            return ResultHolder.success(successMessage, successCoinPairs);
        } else {
            String errorMessage = "Some coin pairs initialization failed";
            return ResultHolder.error(errorMessage, failedCoinPairs);
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
