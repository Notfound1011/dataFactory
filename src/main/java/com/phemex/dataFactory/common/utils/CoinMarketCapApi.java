//package com.phemex.dataFactory.common.utils;
//
///**
// * @author: yuyu.shi
// * @Project: phemex
// * @Package: com.phemex.dataFactory.common.utils.CoinMarketCapApi
// * @Date: 2024年01月16日 18:17
// * @Description:
// */
//import com.alibaba.fastjson2JSONObject;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//
//public class CoinMarketCapApi {
//
//    // 替换为你的CoinMarketCap API密钥
//    private static final String API_KEY = "249873ec-c62c-4637-8cbf-4ee54c727a57";
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        // 设置你想要查询的加密货币对，例如比特币的ID是1
//        String symbol = "MOVR";
//        // 设置你想要转换的货币，例如美元的符号是USD
//        String convertCurrency = "USD";
//
//        // 构建请求URL
//        String url = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=" + symbol + "&convert=" + convertCurrency;
//
//        // 创建HttpClient实例
//        HttpClient client = HttpClient.newHttpClient();
//
//        // 创建请求
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("X-CMC_PRO_API_KEY", API_KEY)
//                .header("Accept", "application/json")
//                .build();
//
//        // 发送请求并获取响应
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        System.out.println(response.body());
//        // 检查响应状态码
//        if (response.statusCode() == 200) {
//            // 解析响应内容
//            JSONObject jsonResponse =  JSONObject.parseObject(response.body());
//            // 获取价格信息（根据API响应结构进行解析）
//            double price = jsonResponse.getJSONObject("data")
//                    .getJSONArray(symbol)
//                    .getJSONObject(0)
//                    .getJSONObject("quote")
//                    .getJSONObject("USD")
//                    .getDouble("price");
//
//            // 打印价格信息
//            System.out.println("The current price is: " + price);
//        } else {
//            System.out.println("Error: " + response.body());
//        }
//    }
//}