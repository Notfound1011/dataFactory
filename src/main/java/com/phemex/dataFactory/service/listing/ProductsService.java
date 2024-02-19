package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.config.PhemexApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.getHeader;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.ProductsService
 * @Date: 2024年01月09日 20:13
 * @Description:
 */
@Service
public class ProductsService {
    private static final Logger log = LoggerFactory.getLogger(ProductsService.class);
    private String host;
    private final Map<String, String> phemexHostMap;
    private JSONObject productData;


    public ProductsService(Map<String, String> phemexHostMap) {
        this.phemexHostMap = phemexHostMap;
        this.productData = new JSONObject();
    }


    public void setEnv(String env) {
        this.host = phemexHostMap.getOrDefault(env.toLowerCase(), "https://api10-phemex.com");
    }

    private void getProduct() {
        try {
            String url = this.host + "/public/products-plus";
            HashMap<String, String> header = getHeader();
            String result = HttpClientUtil.get(url, header, null);
            JSONObject response = JSONObject.parseObject(result);
            if (response != null && response.getIntValue("code") == 0) {
                this.productData = response.getJSONObject("data");
            }
        } catch (Exception e) {
            log.error("Error fetching product data", e);
        }
    }

    public JSONObject getProductData() {
        if (productData.isEmpty()) {
            getProduct();
        }
        return productData;
    }

    // 获取contractV2信息
    public Map<String, JSONObject> getContractSymbolV2() {
        if (this.productData == null || this.productData.isEmpty()) {
            this.getProduct();
        }

        Map<String, JSONObject> contractSymbolsV2 = new HashMap<>();
        if (this.productData != null && this.productData.containsKey("perpProductsV2")) {
            JSONArray perpProductsV2Array = this.productData.getJSONArray("perpProductsV2");
            if (perpProductsV2Array != null) {
                for (int i = 0; i < perpProductsV2Array.size(); i++) {
                    JSONObject product = perpProductsV2Array.getJSONObject(i);
                    if ("PerpetualV2".equals(product.getString("type"))) {
                        contractSymbolsV2.put(product.getString("symbol"), product);
                    }
                }
            }
        }
        return contractSymbolsV2;
    }

    // 获取货币信息
    public Map<String, JSONObject> getCurrency() {
        if (this.productData == null || this.productData.isEmpty()) {
            this.getProduct(); // 确保 productData 被填充
        }

        Map<String, JSONObject> currencyPrecisionMap = new HashMap<>();
        if (this.productData != null && this.productData.containsKey("currencies")) {
            JSONArray currenciesArray = this.productData.getJSONArray("currencies");
            if (currenciesArray != null) {
                for (int i = 0; i < currenciesArray.size(); i++) {
                    JSONObject currencyItem = currenciesArray.getJSONObject(i);
                    currencyPrecisionMap.put(currencyItem.getString("currency"), currencyItem);
                }
            }
        }
        return currencyPrecisionMap;
    }

    // 获取spot symbol信息
    public Map<String, JSONObject> getSpotSymbols() {
        if (this.productData == null || this.productData.isEmpty()) {
            this.getProduct(); // 确保 productData 被填充
        }

        Map<String, JSONObject> spotSymbolMap = new HashMap<>();
        if (this.productData != null && this.productData.containsKey("products")) {
            JSONArray productsArray = this.productData.getJSONArray("products");
            if (productsArray != null) {
                for (int i = 0; i < productsArray.size(); i++) {
                    JSONObject productItem = productsArray.getJSONObject(i);
                    if ("Spot".equals(productItem.getString("type"))) {
                        spotSymbolMap.put(productItem.getString("symbol"), productItem);
                    }
                }
            }
        }
        return spotSymbolMap;
    }

    public static void main(String[] args) {
        // 创建配置类实例
        PhemexApiConfig config = new PhemexApiConfig();
        // 从配置类获取phemexHostMap
        Map<String, String> phemexHostMap = config.phemexHostMap();

        // 创建服务实例
        ProductsService productService = new ProductsService(phemexHostMap);
        // 设置环境
        productService.setEnv("fat2");

        try {
            // 获取产品数据
            JSONObject productData = productService.getProductData();
//            System.out.println(productData);
            Map<String, JSONObject> contractSymbolV2 = productService.getContractSymbolV2();
            Map<String, JSONObject> a = productService.getCurrency();
            Map<String, JSONObject> b = productService.getSpotSymbols();
//            System.out.println(contractSymbolV2);
//            System.out.println(a);
            System.out.println(b);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
