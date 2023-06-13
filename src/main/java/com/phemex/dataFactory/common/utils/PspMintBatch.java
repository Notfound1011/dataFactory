package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.tokenGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.*;
import java.util.HashMap;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;
import static com.phemex.dataFactory.common.utils.RequestTask.getCurrentTime;

public class PspMintBatch {
    public static void main(String[] args) throws Exception {
//        UserRegisterRequest urr = new UserRegisterRequest();
//        urr.setEmailPrefix("stakeloadtest");
//        urr.setEmailSuffix("@yopmail.com");
//        urr.setPassword("Shiyu@123");
//        urr.setNumStart(504);
//        urr.setNumEnd(504);
//        urr.setReqDelayMs(1000);
//        List<RegistrationInfo> aa  = registerUsers(urr);  //注册账号
//        System.out.println(aa);

//        genTokenByLogin(false, true); //通过登录获取token
        deposit(10000, "USDT");  // 充值
    }

    /**
     * @Description: csv的方式，通过登录获取token
     * @Date: 2022/12/30
     * @Param append: true/false 是否追加写入
     * @Param loginOnly: true/false 是否仅执行登录逻辑
     **/
    static void genTokenByLogin(Boolean append, Boolean loginOnly) throws Exception {
        String baseFilePath = "src/main/resources/input/user_login_info.csv";
//        String outputFilePath = "src/main/resources/output/user_login_result.csv";
        String outputFilePath = "src/main/resources/input/token.txt";

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("uid,email,password,token\n");
        int num = 0;

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)))) {
            String str;
            while ((str = bufReader.readLine()) != null) {
                try {
                    // 设置请求头
                    HashMap<String, String> header = getHeader();

                    TokenInfo token = getTokenByLogin(header, str.split(",")[1], str.split(",")[2]);
                    System.out.println(num++ + ": " + token.getResponseHeader() + token.getBody().get("email"));
                    strBuffer.append(str).append(",").append(token.getResponseHeader());
                    strBuffer.append("\n"); // 行与行之间的分割

                    // bind和mint
                    if (!loginOnly) {
                        mint(str, header, token.getResponseHeader());
                    }

                } catch (Exception e) {
                    System.out.println("请求接口时发生错误。");
                    e.printStackTrace();
                    continue; // 继续下一次循环
                }
                Thread.sleep(1000);
            }
        }

        //写入文件
        writeToFile(outputFilePath, strBuffer.toString(), append);
    }

    /**
     * @Description: 通过登录获取token
     * @Date: 2022/12/30
     * @Param null:
     **/
    static TokenInfo getTokenByLogin(HashMap<String, String> header, String email, String password) throws Exception {
        // 设置请求体
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("encryptVersion", 0);
        JSONObject jsonObj = new JSONObject(body);

        String res = HttpClientUtil.jsonPost("https://fat3.phemex.com/api/phemex-user/users/login", jsonObj.toString(), header);
        JSONObject json_res = (JSONObject) JSONObject.parse(res);
        System.out.println(json_res);

        String code = (String) json_res.getJSONObject("data").get("code");
        String url = "https://fat3.phemex.com/api/phemex-user/users/confirm/login" + "?code=" + code + "&mailCode=111111";
        CloseableHttpResponse res2 = HttpClientUtil.httpGet(url, header);
        String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();

        return new TokenInfo(body, responseHeader);
    }

    static class TokenInfo {
        private final HashMap<String, Object> body;
        private final String responseHeader;

        public TokenInfo(HashMap<String, Object> body, String responseHeader) {
            this.body = body;
            this.responseHeader = responseHeader;
        }

        public HashMap<String, Object> getBody() {
            return body;
        }

        public String getResponseHeader() {
            return responseHeader;
        }
    }

    private static void mint(String str, HashMap<String, String> header, String responseHeader) throws Exception {
        header.put("phemex-auth-token", responseHeader);


        HashMap<String, Object> bindBody = new HashMap<>();
        bindBody.put("address", str.split(",")[3]);
        bindBody.put("source", "Phemex");

        JSONObject jsonbindObj = new JSONObject(bindBody);

        String resBind = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-account/soul-pass/wallets/bind", jsonbindObj.toString(), header);
        JSONObject jsonResBind = (JSONObject) JSONObject.parse(resBind);
        System.out.println("bind结果" + jsonResBind);

        String resMint = HttpClientUtil.jsonPost("https://api10-fat3.phemex.com/phemex-account/soul-pass/mint", header);
        JSONObject jsonResMint = (JSONObject) JSONObject.parse(resMint);
        System.out.println("mint结果" + jsonResMint);
    }


    /**
     * @return
     * @Description: PT stake
     * @Date: 2023/6/9
     * @Param token:
     */
    public static String stake(String token) throws Exception {
        // 设置请求头
        HashMap<String, String> header = getHeader();
        header.put("phemex-auth-token", token);

        HashMap<String, Object> stakeBody = new HashMap<>();
        stakeBody.put("projectKey", "PT-STAKE");
        stakeBody.put("amountRq", "11");
        stakeBody.put("expiryTime", 1686700800000L);
        stakeBody.put("gasFeeRq", 0.40650407);

        JSONObject jsonstakeObj = new JSONObject(stakeBody);

        // 打印请求开始时间
        System.out.println("Start Time: " + getCurrentTime());
        String resStake = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-stake/stake", jsonstakeObj.toString(), header);
        JSONObject jsonResStake = (JSONObject) JSONObject.parse(resStake);
        System.out.println("stake结果" + jsonResStake);
        return jsonResStake.toString();
    }
}
