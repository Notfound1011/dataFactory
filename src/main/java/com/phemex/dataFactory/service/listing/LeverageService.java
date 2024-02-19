package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.common.utils.LoadTestCommon;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.LeveragePubRequest;
import com.phemex.dataFactory.request.listing.LeverageRestRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.FundingFeeCheckService
 * @Date: 2024年01月06日 09:22
 * @Description:
 */
@Service
public class LeverageService {
    private static final String POSITION_MODE_API_PATH = "/g-positions/switch-pos-mode-sync";
    private static final String LEVERAGE_API_PATH = "/g-positions/leverage";
    private static final Logger log = LoggerFactory.getLogger(LeverageService.class);
    private final Map<String, String> phemexHostMap;
    private final Map<String, String> phemexPubHostMap;


    public LeverageService(Map<String, String> phemexHostMap, Map<String, String> phemexPubHostMap) {
        this.phemexHostMap = phemexHostMap;
        this.phemexPubHostMap = phemexPubHostMap;
    }

    public ResultHolder runByRestApi(LeverageRestRequest leverageRestRequest) throws Exception {
        String host = phemexHostMap.get(leverageRestRequest.getEnv().toLowerCase());
        HashMap<String, String> header = getHeader();
        LoadTestCommon.TokenInfo token = getTokenByLogin(header, leverageRestRequest.getEmail(), leverageRestRequest.getPassword(), leverageRestRequest.getTotpSecret());
        header.put("phemex-auth-token", token.getResponseHeader());

        List<String> errorList = new ArrayList<>();
        List<String> validList = new ArrayList<>();
        List<String> runResult = new ArrayList<>();
        Set<String> failedSet = new HashSet<>();
        Boolean isSuccess;

        ProductsService productsService = new ProductsService(phemexHostMap);
        productsService.setEnv(leverageRestRequest.getEnv());
        JSONObject productData = productsService.getProductData();

        // 针对perpProductsV2进行操作
        for (Object perpProductsV2 : productData.getJSONArray("perpProductsV2")) {
            JSONObject item = (JSONObject) perpProductsV2;
            if (!leverageRestRequest.getSymbolList().contains(item.getString("symbol"))) {
                continue;
            }
            validList.add(item.getString("symbol"));

            Map<String, Object> modeParams = new HashMap<>();
            modeParams.put("targetPosMode", leverageRestRequest.getPositionMode().getValue());
            modeParams.put("symbol", item.getString("symbol"));

            // 设置持仓模式
            CloseableHttpResponse resMode = HttpClientUtil.httpPut(host + POSITION_MODE_API_PATH, modeParams, null, header);
            log.info(resMode.toString());
            runResult.add(respVerify(item, resMode, "Position Mode", failedSet));

            // 设置杠杆
            Map<String, Object> leverageParams = new HashMap<>();
            if (Objects.equals(leverageRestRequest.getPositionMode().getValue(), "Hedged")) {
                leverageParams.put("longLeverageRr", leverageRestRequest.getLeverage());
                leverageParams.put("shortLeverageRr", leverageRestRequest.getLeverage());
            } else if (Objects.equals(leverageRestRequest.getPositionMode().getValue(), "OneWay")) {
                leverageParams.put("leverageRr", leverageRestRequest.getLeverage());
            }
            leverageParams.put("symbol", item.getString("symbol"));
            CloseableHttpResponse resLeverage = HttpClientUtil.httpPut(host + LEVERAGE_API_PATH, leverageParams, null, header);
            log.info(resLeverage.toString());
            runResult.add(respVerify(item, resLeverage, "Leverage", failedSet));
        }

        return getResultHolder(errorList, validList, runResult, leverageRestRequest.getSymbolList(), failedSet);
    }

    public ResultHolder runByPubApi(LeveragePubRequest leveragePubRequest) throws Exception {
        String host = phemexPubHostMap.get(leveragePubRequest.getEnv().toLowerCase());

        List<String> errorList = new ArrayList<>();
        List<String> validList = new ArrayList<>();
        List<String> runResult = new ArrayList<>();
        Set<String> failedSet = new HashSet<>();

        ProductsService productsService = new ProductsService(phemexHostMap);
        productsService.setEnv(leveragePubRequest.getEnv());
        JSONObject productData = productsService.getProductData();

        // 针对perpProductsV2进行操作
        for (Object perpProductsV2 : productData.getJSONArray("perpProductsV2")) {
            JSONObject item = (JSONObject) perpProductsV2;
            if (!leveragePubRequest.getSymbolList().contains(item.getString("symbol"))) {
                continue;
            }
            validList.add(item.getString("symbol"));

            // 设置持仓模式
            Map<String, Object> modeParams = new HashMap<>();
            modeParams.put("targetPosMode", leveragePubRequest.getPositionMode().getValue());
            modeParams.put("symbol", item.getString("symbol"));
            HashMap<String, String> header = headersPubApi(POSITION_MODE_API_PATH, buildQueryString(modeParams), "", leveragePubRequest.getSecretKey(), leveragePubRequest.getAccessToken());
            CloseableHttpResponse resMode = HttpClientUtil.httpPut(host + POSITION_MODE_API_PATH, modeParams, null, header);
            log.info(resMode.toString());
            runResult.add(respVerify(item, resMode, "Position Mode", failedSet));

            // 设置杠杆
            Map<String, Object> leverageParams = new HashMap<>();
            if (Objects.equals(leveragePubRequest.getPositionMode().getValue(), "Hedged")) {
                leverageParams.put("longLeverageRr", leveragePubRequest.getLeverage());
                leverageParams.put("shortLeverageRr", leveragePubRequest.getLeverage());
            } else if (Objects.equals(leveragePubRequest.getPositionMode().getValue(), "OneWay")) {
                leverageParams.put("leverageRr", leveragePubRequest.getLeverage());
            }
            leverageParams.put("symbol", item.getString("symbol"));
            HashMap<String, String> leverageHeader = headersPubApi(LEVERAGE_API_PATH, buildQueryString(leverageParams), "", leveragePubRequest.getSecretKey(), leveragePubRequest.getAccessToken());
            CloseableHttpResponse resLeverage = HttpClientUtil.httpPut(host + LEVERAGE_API_PATH, leverageParams, null, leverageHeader);
            log.info(resLeverage.toString());
            runResult.add(respVerify(item, resLeverage, "Leverage", failedSet));
        }

        return getResultHolder(errorList, validList, runResult, leveragePubRequest.getSymbolList(), failedSet);
    }

    @NotNull
    private ResultHolder getResultHolder(List<String> errorList, List<String> validList, List<String> runResult, List<String> symbolList, Set<String> failedSet) {
        for (String symbol : symbolList) {
            if (!validList.contains(symbol)) {
                errorList.add(symbol);
            }
        }

        if (!errorList.isEmpty()) {
            return ResultHolder.error("Validation Failed", "有未识别的symbol: " + errorList);
        } else if (!failedSet.isEmpty()) {
            return ResultHolder.error("Validation Failed", "以下symbol执行失败: " + failedSet + " Details: " + runResult);
        } else {
            return ResultHolder.success("所有symbol执行完毕!! " + " Details: " + runResult);
        }
    }

    private String respVerify(JSONObject item, CloseableHttpResponse response, String initKey, Set<String> failedSet) {
        String verifyResult;
        try {
            // 将HttpEntity转换为字符串
            String result = EntityUtils.toString(response.getEntity());
            // 将字符串转换为JSONObject
            JSONObject jsonObject = JSONObject.parseObject(result);
            // 现在可以使用这个JSONObject对象了
            if (response.getStatusLine().getStatusCode() == 200 && jsonObject.getIntValue("code") == 0) {
                verifyResult = item.getString("symbol") + ", 调整 " + initKey + " 成功!";

            } else {
                verifyResult = item.getString("symbol") + ", 调整 " + initKey + " 失败!!!\n" + response.getStatusLine().getStatusCode() + "\n" + jsonObject;
                failedSet.add(item.getString("symbol"));
            }
            Thread.sleep(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return verifyResult;
    }

}
