package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.base.PhemexManageApi;
import com.phemex.dataFactory.request.base.PhemexOpsysApi;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.TokenService
 * @Date: 2024年02月27日 17:48
 * @Description:
 */
@Service
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    // 获取到管理后台的phemex-admin-token
    public Map<String, String> getManageToken(String host, PhemexManageApi phemexManageApi) {
        Map<String, String> tokenMap = new HashMap<>();
        String url = host + "/phemex-admin/phemex-entitlement/login";

        CloseableHttpResponse response;
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-type", "application/json");

            Map<String, Object> json = new HashMap<>();
            json.put("email", phemexManageApi.getUserName());
            json.put("username", phemexManageApi.getUserName());
            json.put("password", phemexManageApi.getPassword());
            json.put("google2fa", "1");

            response = HttpClientUtil.httpPost(url, json, header);

            if (response.getStatusLine().getStatusCode() == 200) {
                tokenMap.put("phemex-admin-token", response.getFirstHeader("phemex-admin-token").getValue());
                log.info(tokenMap.toString());
                return tokenMap;
            } else {
                log.error("ManageConfig获取账号信息失败!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 获取到Opsys的token
    public Map<String, String> getOpsysToken(String host, PhemexOpsysApi phemexOpsysApi) {
        Map<String, String> tokenMap = new HashMap<>();
        String url = host + "/opsys/user/login/";

        CloseableHttpResponse response;
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-type", "application/json");

            Map<String, Object> json = new HashMap<>();
            json.put("username", phemexOpsysApi.getUserName());
            json.put("password", phemexOpsysApi.getPassword());

            response = HttpClientUtil.httpPost(url, json, header);

            if (response.getStatusLine().getStatusCode() == 200) {
                String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject jsonResponse = JSONObject.parseObject(responseString);
                //对象中提取 "token" 字符串
                String token = jsonResponse.getJSONObject("data").getString("token");

                tokenMap.put("Authorization", "Bearer "+token);
                log.info(tokenMap.toString());
                return tokenMap;
            } else {
                log.error("Opsys获取账号信息失败!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
