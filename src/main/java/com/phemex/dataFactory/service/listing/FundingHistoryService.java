package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.common.utils.LoadTestCommon;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.FundingHistoryPubRequest;
import com.phemex.dataFactory.request.listing.FundingHistoryRestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.FundingFeeCheckService
 * @Date: 2024年01月06日 09:22
 * @Description:
 */
@Service
public class FundingHistoryService {
    private static final String FUNDING_LIST_API_PATH = "/phemex-user/order/v2/fundingList";
    private static final Logger log = LoggerFactory.getLogger(FundingHistoryService.class);
    private final Map<String, String> phemexHostMap;
    private final Map<String, String> phemexPubHostMap;

    public FundingHistoryService(Map<String, String> phemexHostMap, Map<String, String> phemexPubHostMap) {
        this.phemexHostMap = phemexHostMap;
        this.phemexPubHostMap = phemexPubHostMap;
    }


    private ResultHolder checkFunding(List<Map<String, Object>> checkData, List<String> symbolList, Long timestamp) {
        Boolean isSuccess;
        // 找出checkData中timestamp一样的Symbol --> symbolsWithData
        Set<String> symbolsWithData = checkData.stream()
                .filter(item -> timestamp.equals(((Number) item.get("createdAt")).longValue() / 1000))
                .map(item -> (String) item.get("symbol"))
                .collect(Collectors.toSet());
        // 找出symbolList中，不在上面symbolsWithData的Symbol
        List<String> missingSymbols = symbolList.stream()
                .filter(symbol -> !symbolsWithData.contains(symbol))
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder();
        if (!missingSymbols.isEmpty()) {
            result.append("以下symbol 未找到funding记录: ").append(String.join(",", missingSymbols));
            isSuccess = false;
        } else {
            result.append("均找到funding记录: ").append(String.join(",", symbolList));
            isSuccess = true;
        }

        // 找出哪些 symbol，在同一 createdAt 时间戳下出现了超过两次
        Map<String, Map<String, Integer>> cacheData = new HashMap<>();
        for (Map<String, Object> item : checkData) {
            String createdAt = String.valueOf(item.get("createdAt"));
            String symbol = (String) item.get("symbol");
            cacheData.putIfAbsent(createdAt, new HashMap<>());
            cacheData.get(createdAt).merge(symbol, 1, Integer::sum);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        for (Map.Entry<String, Map<String, Integer>> entry : cacheData.entrySet()) {
            long key = Long.parseLong(entry.getKey());
            Map<String, Integer> value = entry.getValue();
            List<Map<String, Integer>> tmpList = value.entrySet().stream()
                    .filter(e -> e.getValue() > 2)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    .entrySet().stream().map(e -> Map.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            if (!tmpList.isEmpty()) {
                result.append("当前时间: " + formatter.format(Instant.ofEpochSecond(key / 1000)) + " " + key + ", 有重复symbol: ").append(tmpList);
            }
        }
        String resultString = result.toString();
        return isSuccess ? ResultHolder.success(resultString) : ResultHolder.error("Validation Failed", resultString);
    }

    public ResultHolder checkFundingByRestApi(FundingHistoryRestRequest fundingHistoryRestRequest) throws Exception {
        HashMap<String, String> header = getHeader();
        LoadTestCommon.TokenInfo token = getTokenByLogin(header, fundingHistoryRestRequest.getEmail(), fundingHistoryRestRequest.getPassword(), fundingHistoryRestRequest.getTotpSecret());
        header.put("phemex-auth-token", token.getResponseHeader());
        HashMap<String, Object> params = new HashMap<>();
        params.put("currency", "USDT");
        params.put("limit", fundingHistoryRestRequest.getLimit());
        params.put("symbol", "");
        params.put("withCount", true);
        String path = FUNDING_LIST_API_PATH;
        String url = phemexHostMap.get(fundingHistoryRestRequest.getEnv().toLowerCase()) + path;

        String result = HttpClientUtil.get(url, header, params);
        JSONObject response = JSONObject.parseObject(result);
        return getDataList(fundingHistoryRestRequest.getSymbolList(), fundingHistoryRestRequest.getTimestamp(), response);
    }


    public ResultHolder checkFundingByPubApi(FundingHistoryPubRequest fundingHistoryPubRequest) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("currency", "USDT");
        params.put("limit", fundingHistoryPubRequest.getLimit());
        params.put("symbol", "");
        params.put("withCount", true);
        String path = FUNDING_LIST_API_PATH;
        String url = phemexPubHostMap.get(fundingHistoryPubRequest.getEnv().toLowerCase()) + path;

        String queryString = buildQueryString(params);
        HashMap<String, String> header = headersPubApi(path, queryString, "", fundingHistoryPubRequest.getSecretKey(), fundingHistoryPubRequest.getAccessToken());
        String result = HttpClientUtil.get(url, header, params);
        JSONObject response = JSONObject.parseObject(result);
        return getDataList(fundingHistoryPubRequest.getSymbolList(), fundingHistoryPubRequest.getTimestamp(), response);
    }


    private ResultHolder getDataList(List<String> symbolList, Long timestamp, JSONObject response) {
        JSONArray dataObject = (JSONArray) response.getJSONObject("data").get("rows");
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Object obj : dataObject) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObj = (JSONObject) obj;
                dataList.add(jsonObj);
            } else {
                log.warn("Expected JSONObject but found: " + obj.getClass().getSimpleName());
            }
        }
        return checkFunding(dataList, symbolList, timestamp);
    }
}
