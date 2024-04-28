package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.model.AdminAccount;
import com.phemex.dataFactory.request.base.PhemexAdminApi;
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
    private final AdminAccountService adminAccountService;

    public TokenService(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    // 获取到管理后台的phemex-admin-token
    public Map<String, String> getManageToken(String host, PhemexAdminApi phemexAdminApi) {
        Map<String, String> tokenMap = new HashMap<>();
        String url = host + "/phemex-admin/phemex-entitlement/login";

        CloseableHttpResponse response;
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-type", "application/json");

            AdminAccount adminAccount = adminAccountService.getAdminAccount(phemexAdminApi.getOwner(), phemexAdminApi.getAccountType());

            Map<String, Object> json = new HashMap<>();
            json.put("email", adminAccount.getUsername());
            json.put("username", adminAccount.getUsername());
            json.put("password", adminAccount.getPassword());
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
    public Map<String, String> getOpsysToken(String host, PhemexAdminApi phemexAdminApi) {
        Map<String, String> tokenMap = new HashMap<>();
        String url = host + "/opsys/user/login/";
        AdminAccount adminAccount = adminAccountService.getAdminAccount(phemexAdminApi.getOwner(), phemexAdminApi.getAccountType());

        CloseableHttpResponse response;
        try {
            Map<String, String> header = new HashMap<>();
            header.put("Accept", "application/json");
            header.put("Content-type", "application/json");

            Map<String, Object> json = new HashMap<>();
            json.put("username", adminAccount.getUsername());
            json.put("password", adminAccount.getPassword());

            response = HttpClientUtil.httpPost(url, json, header);

            if (response.getStatusLine().getStatusCode() == 200) {
                String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject jsonResponse = JSONObject.parseObject(responseString);
                //对象中提取 "token" 字符串
                String token = jsonResponse.getJSONObject("data").getString("token");

                tokenMap.put("Authorization", "Bearer " + token);
                log.info("Token: {}", tokenMap);
                return tokenMap;
            } else {
                log.error("Opsys获取账号信息失败: {}", response);
                throw new RuntimeException("Opsys获取账号信息失败，请检查账号密码是否正确!"); // 抛出一个运行时异常
            }
        } catch (Exception e) {
            throw new RuntimeException("获取Opsys Token时发生错误，请检查账号密码是否正确!");
        }
    }
}
