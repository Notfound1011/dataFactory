package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.config.PhemexApiConfig;
import com.phemex.dataFactory.request.ResultHolder;
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
 * @Package: com.phemex.dataFactory.service.listing.MarketDataService
 * @Date: 2024年02月04日 16:55
 * @Description:
 */
@Service
public class MarketDataService {
    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);
    private final Map<String, String> phemexHostMap;

    public MarketDataService(Map<String, String> phemexHostMap) {
        this.phemexHostMap = phemexHostMap;
    }


    public ResultHolder getTrendData(List<String> symbolList, String env) {
        String path = "/phemex-user/public/md/v2/trend";
        String baseUrl = phemexHostMap.get(env);
        String url = baseUrl + path + "?symbol=" + String.join(",", symbolList);
        List<String> nullList = new ArrayList<>();
        List<String> remainingSymbols = new ArrayList<>(symbolList);

        try {
            String response = HttpClientUtil.get(url);
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject item = dataArray.getJSONObject(i);
                remainingSymbols.remove(item.getString("symbol"));
                if (item.getJSONArray("prices").isEmpty()) {
                    nullList.add(item.getString("symbol"));
                }
            }
        } catch (Exception e) {
            log.warn("An error occurred while fetching trend data: ", e);
            return ResultHolder.error("获取trend数据时发生错误", e.getMessage());
        }

        if (!remainingSymbols.isEmpty()) {
            return ResultHolder.error("剩余未返回的symbol数量: " + remainingSymbols.size(), remainingSymbols);
        } else if (!nullList.isEmpty()) {
            return ResultHolder.error("发现空值symbol数量: " + nullList.size(), nullList);
        } else {
            return ResultHolder.success("所有symbol均有返回数据");
        }
    }

    public ResultHolder getKlineData(List<String> symbolList, String env) {
        Map<String, List<Integer>> validData = new HashMap<>();
        long endTime = System.currentTimeMillis() / 1000L;
        int[] resolutionList = {60, 180, 300, 900, 1800, 3600, 7200, 10800, 14400, 21600, 43200};  // 86400, 604800, 2592000, 7776000, 31104000

        String baseUrl = phemexHostMap.get(env);
        String endpoint = "/phemex-user/public/md/v2/kline/list";

        try {
            for (String symbol : symbolList) {
                for (int resolution : resolutionList) {
                    String url = baseUrl + endpoint + "?symbol=" + symbol + "&resolution=" + resolution + "&from=" + (endTime - 24 * 60 * 60) + "&to=" + endTime;
                    System.out.println(url);
                    String response = HttpClientUtil.get(url);
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("rows");
                    if (!dataArray.isEmpty()) {
                        validData.computeIfAbsent(symbol, k -> new ArrayList<>()).add(resolution);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("An error occurred while fetching kline data for " + ": " + e);
            return ResultHolder.error("获取kline数据时发生错误", e.getMessage());
        }

        if (!validData.isEmpty()) {
            return ResultHolder.success("发现有值symbol数量: " + validData.size(), validData);
        } else {
            return ResultHolder.error("所有Symbol均未获取到kline数据");
        }
    }

    public static void main(String[] args) {
        List<String> symbolList = new ArrayList();
        symbolList.add("BTCUSDT");
        symbolList.add("sBTCUSDT");

        PhemexApiConfig config = new PhemexApiConfig();
        Map<String, String> phemexHostMap = config.phemexHostMap();
        MarketDataService marketDataService = new MarketDataService(phemexHostMap);
//        marketDataService.getTrendData(symbolList, "fat2");
        marketDataService.getKlineData(symbolList, "fat2");
    }
}
