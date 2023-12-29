package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static com.phemex.dataFactory.common.utils.GoogleAuthenticator.generateTotp;
import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils
 * @Date: 2023年06月09日 10:56
 * @Description:
 */
public class ApiKeysCreate {
    public static void main(String[] args) throws Exception {
        loginAndCreateApiKey(); // 串行方式，先登录再创建apikey
    }

    /**
     * @Description: 串行的方式发起登录，可在登录后执行其他逻辑，效率较低
     * @Date: 2023/06/09
     * @Param loginOnly: true/false 是否仅执行登录逻辑
     **/
    static void loginAndCreateApiKey() throws Exception {
        String baseFilePath = "src/main/resources/input/emails.txt";
//        String outputFilePath = "src/main/resources/output/user_login_result.csv";
        String outputFilePath = "src/main/resources/output/secretKey.csv";

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("email,x-phemex-access-token,secretKey\n");
        int num = 0;

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)))) {
            String str;
            while ((str = bufReader.readLine()) != null) {
                try {
                    // 设置请求头
                    HashMap<String, String> header = getHeader();

                    TokenInfo token = getTokenByLogin(header, str.split(",")[0], "Shiyu@123");
                    System.out.println(num++ + ": " + token.getResponseHeader() + token.getBody().get("email"));

                    // 也可以加入其他逻辑
                    // ApiKeyCreate
                    JSONObject data = ApiKeyCreate(header, token.getResponseHeader());
                    // 提取 secretKey 和 token
                    String secretKey = data.getString("secretKey");
                    String tokenId = data.getString("token");

                    strBuffer.append(str).append(",").append(tokenId).append(",").append(secretKey);
                    strBuffer.append("\n"); // 行与行之间的分割

                } catch (Exception e) {
                    System.out.println("请求接口时发生错误。");
                    e.printStackTrace();
                    continue; // 继续下一次循环
                }
                Thread.sleep(1000);
            }
        }

        //写入文件
        writeToFile(outputFilePath, strBuffer.toString(), false);
    }


    private static JSONObject ApiKeyCreate(HashMap<String, String> header, String responseHeader) throws Exception {
        header.put("phemex-auth-token", responseHeader);


        HashMap<String, Object> createBody = new HashMap<>();
        createBody.put("operatePrivilege", 1);
        createBody.put("comments", "test");
        createBody.put("ipAddresses", "");
        createBody.put("type", 0);

        JSONObject jsonCreateObj = new JSONObject(createBody);
        System.out.println(jsonCreateObj);

        String totpCode = generateTotp("4S6MKCZ6NG53AZHS");
        String url = "https://api10-fat2.phemex.com/phemex-user/users/keys/create?mailCode=&otpCode=" + totpCode;
        String resCreate = HttpClientUtil.jsonPost(url, jsonCreateObj.toString(), header);
        JSONObject jsonResCreate = (JSONObject) JSONObject.parse(resCreate);
        System.out.println("Api Create结果" + jsonResCreate);

        JSONObject data = jsonResCreate.getJSONObject("data");
        return data;
    }
}
