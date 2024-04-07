package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.config.PhemexApiConfig;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.PositionPubRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.headersPubApi;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.PositionService
 * @Date: 2024年01月19日 14:33
 * @Description:
 */
@Service
public class PositionService {
    private static final String ORDERS_API_PATH = "/g-orders";

    @Value("${service.url}")
    private String serviceUrl;
    private static final Logger log = LoggerFactory.getLogger(PositionService.class);
    private final Map<String, String> phemexHostMap;
    private final Map<String, String> phemexPubHostMap;

    public PositionService(Map<String, String> phemexHostMap, Map<String, String> phemexPubHostMap) {
        this.phemexHostMap = phemexHostMap;
        this.phemexPubHostMap = phemexPubHostMap;
    }


    /**
     * @Description: 开仓
     * @Date: 2024/1/19
     * @Param positionPubRequest:
     **/
    public ResultHolder openPosition(PositionPubRequest positionPubRequest) {
        boolean sideRand = false;
        String env = positionPubRequest.getEnv();
//        String side = positionPubRequest.getSide();
        List<String> sides = positionPubRequest.getSides();
        List<String> symbolList = positionPubRequest.getSymbolList();
        String ordType = positionPubRequest.getOrdType();
        String orderQty = positionPubRequest.getOrderQty();

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
            String indexSymbol = "." + symbol;
            Map<String, Object> params = new HashMap<>();
            params.put("symbolList", indexSymbol);
            params.put("getAll", "false");
            params.put("env", env);
            String priceRp;
            String url = "http://" + serviceUrl + ":6200/public/test-data/tools/phemex/get-index-price";
            try {
                String result = HttpClientUtil.get(url, null, params);
                JSONObject response = JSONObject.parseObject(result);
                priceRp = response.getJSONObject("data").getString(indexSymbol);
                log.info("get index success: " + result);
            } catch (Exception e) {
                log.error("get index failed: ", e);
                throw new RuntimeException(e);
            }

            for (String side : sides) {
                Map<String, Object> json = getOpenPositionJson(env, symbol, ordType, side, orderQty, priceRp);
                CloseableHttpResponse response = gOrders(positionPubRequest, json);
                runResult.add(respVerify(symbol, response, side, ordType, failedSet));
            }
        }

        if (!failedSet.isEmpty()) {
            return ResultHolder.error("Validation Failed", "以下symbol执行失败: " + failedSet + " Details: " + runResult);
        } else {
            return ResultHolder.success("所有symbol执行完毕!! " + " Details: " + runResult);
        }
    }

    /**
     * @Description: 清仓
     * @Date: 2024/1/19
     * @Param positionPubRequest:
     **/
    public ResultHolder clearance(PositionPubRequest positionPubRequest) {
        String env = positionPubRequest.getEnv();
        List<String> symbolList = positionPubRequest.getSymbolList();
        List<String> sides = Arrays.asList("Buy", "Sell");

        List<String> runResult = new ArrayList<>();
        Set<String> failedSet = new HashSet<>();

        ProductsService productService = new ProductsService(phemexHostMap);
        // 设置环境
        productService.setEnv(env);
        Map<String, JSONObject> contractSymbolV2 = productService.getContractSymbolV2();

        for (String symbol : symbolList) {

            String orderQty = contractSymbolV2.get(symbol).getString("maxOrderQtyRq");

            Map<String, Object> json = new HashMap<>();
            json.put("symbol", symbol);
            json.put("orderQty", orderQty);
            json.put("ordType", "Market");
            json.put("timeInForce", "ImmediateOrCancel");
            json.put("closeOnTrigger", true);
            json.put("actionBy", "UserClosePosition");

            for (String side : sides) {
                String posSide = side.equalsIgnoreCase("sell") ? "Long" : "Short";
                json.put("clOrdID", UUID.randomUUID().toString());
                json.put("side", side);
                json.put("posSide", posSide);
                CloseableHttpResponse response = gOrders(positionPubRequest, json);
                runResult.add(respVerify(symbol, response, side, "Close All Positions", failedSet));
            }
        }

        if (!failedSet.isEmpty()) {
            return ResultHolder.error("Validation Failed", "以下symbol执行失败: " + failedSet + " Details: " + runResult);
        } else {
            return ResultHolder.success("所有symbol执行完毕!! " + " Details: " + runResult);
        }
    }

    // 下单接口
    private CloseableHttpResponse gOrders(PositionPubRequest positionPubRequest, Map<String, Object> json) {
        String path = ORDERS_API_PATH;
        String url = phemexPubHostMap.get(positionPubRequest.getEnv().toLowerCase()) + path;

        HashMap<String, String> header = headersPubApi(path, "", JSON.toJSONString(json), positionPubRequest.getSecretKey(), positionPubRequest.getAccessToken());
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
                verifyResult = type + ": " +  symbol + " " + side + " " + " execution failed!!" + " HTTP code: " + response.getStatusLine().getStatusCode() + jsonObject;
                failedSet.add(symbol);
            }
            Thread.sleep(100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return verifyResult;
    }

    /**
     * @Description: 设置请求体
     * @Date: 2024/1/19
     * @Param symbol: 币对
     * @Param ordType: Limit,Market
     * @Param side: Buy,Sell
     * @Param orderQty: 下单数量
     * @Param priceRp: 下单价格
     **/
    public Map<String, Object> getOpenPositionJson(String env, String symbol, String ordType, String side, String
            orderQty, String priceRp) {
        if (orderQty == null) {
            // 创建服务实例
            ProductsService productService = new ProductsService(phemexHostMap);
            // 设置环境
            productService.setEnv(env);

            Map<String, JSONObject> contractSymbolV2 = productService.getContractSymbolV2();
            orderQty = contractSymbolV2.get(symbol).getString("qtyStepSize");
        }

        String timeInForce = Objects.equals(ordType, "Market") ? "ImmediateOrCancel" : "GoodTillCancel";
        String posSide = side.equalsIgnoreCase("buy") ? "Long" : "Short";

        Map<String, Object> openPositionJson = new HashMap<>();
        openPositionJson.put("actionBy", "FromOrderPlacement");
        openPositionJson.put("symbol", symbol);
        openPositionJson.put("side", side);
        openPositionJson.put("reduceOnly", false);
        openPositionJson.put("ordType", ordType);
        openPositionJson.put("timeInForce", timeInForce);
        openPositionJson.put("orderQtyRq", orderQty);
        openPositionJson.put("displayQtyRq", orderQty);
        openPositionJson.put("priceRp", priceRp);
        openPositionJson.put("posSide", posSide);
        openPositionJson.put("clOrdID", UUID.randomUUID());
        return openPositionJson;
    }


    public static void main(String[] args) throws Exception {
        // 创建配置类实例
        PhemexApiConfig config = new PhemexApiConfig();
        // 从配置类获取phemexHostMap
        Map<String, String> phemexHostMap = config.phemexHostMap();
        Map<String, String> phemexPubHostMap = config.phemexPubHostMap();

        // 创建服务实例
        PositionService positionService = new PositionService(phemexHostMap, phemexPubHostMap);

        PositionPubRequest aa = new PositionPubRequest();
        aa.setEnv("fat2");
        aa.setSymbolList(Collections.singletonList("SOLUSDT"));
        aa.setSides(Arrays.asList("Buy", "Sell"));
        aa.setOrderQty("0.01");
        aa.setOrdType("Limit");
        aa.setSecretKey("hn0kYOsRXsubW4e36ze4qnBmErStvRakIqO_wuSM35kzYmZmYmU2NS1jNGI0LTRhYTUtYTliYi05YTRlNDM3NmY2MGQ");
        aa.setAccessToken("738fdcc6-5319-4cb0-95f0-664bcfe5ba3f");
        System.out.println(positionService.openPosition(aa));


//        PositionPubRequest bb = new PositionPubRequest();
//        bb.setEnv("fat2");
//        bb.setSymbolList(Collections.singletonList("SOLUSDT"));
//        bb.setSecretKey("hn0kYOsRXsubW4e36ze4qnBmErStvRakIqO_wuSM35kzYmZmYmU2NS1jNGI0LTRhYTUtYTliYi05YTRlNDM3NmY2MGQ");
//        bb.setAccessToken("738fdcc6-5319-4cb0-95f0-664bcfe5ba3f");
//
//        System.out.println(positionService.clearance(bb));
    }
}
