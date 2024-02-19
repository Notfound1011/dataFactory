package com.phemex.dataFactory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.config.PhemexApiConfig
 * @Date: 2024年01月05日 17:29
 * @Description:
 */
@Configuration
public class PhemexApiConfig {

    @Bean
    public Map<String, String> phemexHostMap() {
        Map<String, String> hostMap = new HashMap<>();
        hostMap.put("fat", "https://fat.phemex.com");
        hostMap.put("fat1", "https://api10-fat.phemex.com");
        hostMap.put("fat2", "https://api10-fat2.phemex.com");
        hostMap.put("fat3", "https://api10-fat3.phemex.com");
        hostMap.put("ea", "https://api10-ea.phemex.com");
        hostMap.put("test_net", "https://testnet.phemex.com");
        hostMap.put("testnet", "https://testnet.phemex.com");
        hostMap.put("prod", "https://api10-phemex.com");
        return hostMap;
    }

    @Bean
    public Map<String, String> phemexPubHostMap() {
        Map<String, String> hostMap = new HashMap<>();
        hostMap.put("fat", "https://fat-api.phemex.com");
        hostMap.put("fat1", "https://fat-api.phemex.com");
        hostMap.put("fat2", "https://fat2-api.phemex.com");
        hostMap.put("fat3", "https://fat3-api.phemex.com");
        hostMap.put("testnet", "https://testnet-api.phemex.com");
        hostMap.put("ea", "https://api.phemex.com");
        hostMap.put("prod", "https://api.phemex.com");
        return hostMap;
    }

    @Bean
    public Map<String, String> phemexManageHostMap() {
        Map<String, String> hostMap = new HashMap<>();
        hostMap.put("fat", "https://fat.5810a957f4121923619d86408a6e07d2.me");
        hostMap.put("fat1", "https://fat.5810a957f4121923619d86408a6e07d2.me");
        hostMap.put("fat2", "https://fat.5810a957f4121923619d86408a6e07d2.me:444");
        hostMap.put("fat3", "https://fat.5810a957f4121923619d86408a6e07d2.me:446");
        hostMap.put("ea", "https://a.5810a957f4121923619d86408a6e07d2.me:444");
        hostMap.put("prod", "https://a.5810a957f4121923619d86408a6e07d2.me:444");
        return hostMap;
    }
}
