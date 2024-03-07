package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.SpotTradePubRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.headersPubApi;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.SpotTradeService
 * @Date: 2024年02月18日 11:36
 * @Description:
 */
@Service
public class SpotTradeService {
    private static final String ORDERS_API_PATH = "/spot/orders";

    @Value("${service.url}")
    private String serviceUrl;
    private static final Logger log = LoggerFactory.getLogger(SpotTradeService.class);
    private final Map<String, String> phemexHostMap;
    private final Map<String, String> phemexPubHostMap;


    public SpotTradeService(Map<String, String> phemexHostMap, Map<String, String> phemexPubHostMap) {
        this.phemexHostMap = phemexHostMap;
        this.phemexPubHostMap = phemexPubHostMap;
    }

    public ResultHolder createOrder(SpotTradePubRequest spotTradePubRequest) {
        String env = spotTradePubRequest.getEnv();
        List<String> sides = spotTradePubRequest.getSides();
        List<String> symbolList = spotTradePubRequest.getSymbolList();
        String ordType = spotTradePubRequest.getOrdType();
        String qtyType = spotTradePubRequest.getQtyType();
        String quoteQtyEv = spotTradePubRequest.getQuoteQtyEv();
        String baseQtyEv = spotTradePubRequest.getBaseQtyEv();

        if (qtyType == null) {
            qtyType = "ByQuote";
        } else if (qtyType.equals("ByQuote")) {
            qtyType = "ByQuote";
        } else if (qtyType.equals("ByBase")) {
            qtyType = "ByBase";
        } else {
            throw new IllegalArgumentException("Invalid qtyType input: " + ordType);
        }

        List<String> runResult = new ArrayList<>();
        Set<String> failedSet = new HashSet<>();

        if (ordType == null) {
            ordType = "Market";
        } else if (ordType.equalsIgnoreCase("market")) {
            ordType = "Market";
        } else if (ordType.equalsIgnoreCase("limit")) {
            ordType = "Limit";
        } else {
            throw new IllegalArgumentException("Invalid ordType input: " + ordType);
        }

        for (String symbol : symbolList) {
            String indexSymbol = symbol;
            // 去除首字母小写的's' ,  去除结尾的'USDT'
            if (indexSymbol.startsWith("s")) {
                indexSymbol = indexSymbol.substring(1);
            }
            if (indexSymbol.endsWith("USDT")) {
                indexSymbol = indexSymbol.substring(0, indexSymbol.length() - "USDT".length());
            }

            indexSymbol = "." + indexSymbol;

            Map<String, Object> params = new HashMap<>();
            params.put("symbolList", indexSymbol);
            params.put("getAll", "false");
            params.put("env", env);
            String priceRp;
            String url = "http://" + serviceUrl + ":6200/public/test-data/tools/get-index-price";
            try {
                String result = HttpClientUtil.get(url, null, params);
                JSONObject response = JSONObject.parseObject(result);
                priceRp = response.getJSONObject("data").getString(indexSymbol);
                if (priceRp == null) {
                    priceRp = "1";
                }
                log.info("get index success: " + result);
            } catch (Exception e) {
                log.error("get index failed: ", e);
                throw new RuntimeException(e);
            }

            for (String side : sides) {
                Map<String, Object> json = getCreateOrderJson(env, symbol, ordType, side, qtyType, quoteQtyEv, baseQtyEv, priceRp);
                log.info(json.toString());
                CloseableHttpResponse response = orders(spotTradePubRequest, json);
                runResult.add(respVerify(symbol, response, side, ordType, failedSet));
            }
        }

        if (!failedSet.isEmpty()) {
            return ResultHolder.error("Validation Failed", "以下symbol执行失败: " + failedSet + " Details: " + runResult);
        } else {
            return ResultHolder.success("所有symbol执行完毕!! " + " Details: " + runResult);
        }
    }


    // 下单接口
    private CloseableHttpResponse orders(SpotTradePubRequest spotTradePubRequest, Map<String, Object> json) {
        String path = ORDERS_API_PATH;
        String url = phemexPubHostMap.get(spotTradePubRequest.getEnv().toLowerCase()) + path;

        HashMap<String, String> header = headersPubApi(path, "", JSON.toJSONString(json), spotTradePubRequest.getSecretKey(), spotTradePubRequest.getAccessToken());
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtil.httpPost(url, json, header);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * @Description: 验证接口返回结果
     * @Date: 2024/1/19
     * @Param symbol:
     * @Param response:
     * @Param side:
     * @Param orderQty:
     * @Param failedSet:
     **/
    private String respVerify(String symbol, CloseableHttpResponse response, String side, String
            type, Set<String> failedSet) {
        String verifyResult;
        try {
            // 将HttpEntity转换为字符串
            String result = EntityUtils.toString(response.getEntity());
            // 将字符串转换为JSONObject
            JSONObject jsonObject = JSONObject.parseObject(result);
            // 现在可以使用这个JSONObject对象了
            if (response.getStatusLine().getStatusCode() == 200 && jsonObject.getIntValue("code") == 0) {
                verifyResult = type + ": " + symbol + " " + side + " " + " executed successfully!!";
            } else {
                verifyResult = type + ": " + symbol + " " + side + " " + " execution failed!!" + " HTTP code: " + response.getStatusLine().getStatusCode() + jsonObject;
                failedSet.add(symbol);
            }
            Thread.sleep(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return verifyResult;
    }


    public Map<String, Object> getCreateOrderJson(String env, String symbol, String ordType, String side, String qtyType, String quoteQtyEv, String baseQtyEv, String priceRp) {

        // 创建服务实例
        ProductsService productService = new ProductsService(phemexHostMap);
        // 设置环境
        productService.setEnv(env);
        Map<String, JSONObject> spotSymbols = productService.getSpotSymbols();
        String quoteQty = spotSymbols.get(symbol).getString("minOrderValueEv");
        Integer priceScale = Integer.parseInt(spotSymbols.get(symbol).getString("priceScale"));
        Integer pricePrecision = Integer.parseInt(spotSymbols.get(symbol).getString("pricePrecision"));

        String timeInForce = Objects.equals(ordType, "Market") ? "ImmediateOrCancel" : "GoodTillCancel";


        Map<String, Object> spotOrderJson = new HashMap<>();
        spotOrderJson.put("business", "SPOT");
        spotOrderJson.put("actionBy", "FromOrderPlacement");
        spotOrderJson.put("symbol", symbol);
        spotOrderJson.put("side", side);
        spotOrderJson.put("ordType", ordType);
        spotOrderJson.put("qtyType", qtyType);
        spotOrderJson.put("timeInForce", timeInForce);

        // 根据pricePrecision处理价格，并放大指定倍数
        BigDecimal priceRpDecimal = new BigDecimal(priceRp).setScale(pricePrecision, RoundingMode.HALF_UP);
        BigDecimal priceEp = priceRpDecimal.multiply(BigDecimal.TEN.pow(priceScale)).setScale(0, RoundingMode.HALF_UP);
        spotOrderJson.put("priceEp", priceEp);
        if (baseQtyEv == null && qtyType.equals("ByBase")) {
            BigDecimal quoteQtyDecimal = new BigDecimal(quoteQty);
            BigDecimal baseQty = quoteQtyDecimal.divide(priceRpDecimal, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("2")).setScale(0, RoundingMode.HALF_UP);
            spotOrderJson.put("baseQtyEv", baseQty);
        }
        if (quoteQtyEv == null && qtyType.equals("ByQuote")) {
            spotOrderJson.put("quoteQtyEv", quoteQty);
        }
        spotOrderJson.put("clOrdID", UUID.randomUUID());
        return spotOrderJson;
    }
}
