package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.phemex.dataFactory.request.UserRegisterRequest;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

import static com.phemex.dataFactory.common.utils.EthereumWalletGenerator.genWalletAddress;
import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;
import static com.phemex.dataFactory.common.utils.RequestTask.getCurrentTime;
import static com.phemex.dataFactory.service.UserRegisterService.registerUsers;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils
 * @Date: 2023年06月09日 10:56
 * @Description:
 */
public class PspMintBatch {
    public static void main(String[] args) throws Exception {
        UserRegisterRequest urr = new UserRegisterRequest();
        urr.setEmailPrefix("syy");
        urr.setEmailSuffix("8601405271@gmail.com");
        urr.setPassword("Shiyu@123");
        urr.setNumStart(1);
        urr.setNumEnd(1);
//        urr.setReferralCode("FJNDV");
//        urr.setNumList(Arrays.asList(1067,1184));
        urr.setReqDelayMs(10);
        registerUsers(urr);  //注册账号

//        genTokenByLogin(false, true); //通过登录获取token
//        loginAndMint(); // 串行方式，先登录再mint
//        deposit(100000, "USDT");  // 充值

//        stakeMock("1848615");
    }

    /**
     * @Description: 串行的方式发起登录，可在登录后执行其他逻辑，效率较低
     * @Date: 2023/06/09
     * @Param loginOnly: true/false 是否仅执行登录逻辑
     **/
    static void loginAndMint() throws Exception {
        String baseFilePath = "src/main/resources/input/emails.txt";
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

                    TokenInfo token = getTokenByLogin(header, str.split(",")[0], "Shiyu@123");
                    System.out.println(num++ + ": " + token.getResponseHeader() + token.getBody().get("email"));
                    strBuffer.append(str).append(",").append(token.getResponseHeader());
                    strBuffer.append("\n"); // 行与行之间的分割

                    // 也可以加入其他逻辑
                    // bind和mint
                    mint(genWalletAddress(), header, token.getResponseHeader());

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


    static void loginAndOthers(String param) throws Exception {
        try {
            // 设置请求头
            HashMap<String, String> header = getHeader();

            TokenInfo token = getTokenByLogin(header, param.split(",")[1], "Shiyu@123");
            System.out.println(token.getBody().get("email") + ": login success");

            // bind和mint
//            mint(param.split(",")[0], header, token.getResponseHeader());

            // redeem
            redeem(header, token.getResponseHeader());

            // stake
            stake(token.getResponseHeader());

            // margin open
//            marginOpen(header,token.getResponseHeader());
        } catch (Exception e) {
            System.out.println("请求接口时发生错误。");
            e.printStackTrace();
        }

    }

    private static void mint(String address, HashMap<String, String> header, String responseHeader) throws Exception {
        header.put("phemex-auth-token", responseHeader);


        HashMap<String, Object> bindBody = new HashMap<>();
        bindBody.put("address", address);
        bindBody.put("source", "Phemex");

        JSONObject jsonbindObj = new JSONObject(bindBody);
        System.out.println(jsonbindObj);

        String resBind = HttpClientUtil.jsonPost("https://api10-fat.phemex.com/phemex-account/soul-pass/wallets/bind", jsonbindObj.toString(), header);
        JSONObject jsonResBind = (JSONObject) JSONObject.parse(resBind);
        System.out.println("bind结果" + jsonResBind);

        String resMint = HttpClientUtil.jsonPost("https://api10-fat.phemex.com/phemex-account/soul-pass/mint", header);
        JSONObject jsonResMint = (JSONObject) JSONObject.parse(resMint);
        System.out.println("mint结果" + jsonResMint);
    }


    /**
     * @return String
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
        stakeBody.put("amountRq", "10000");
        stakeBody.put("expiryTime", 1687910400000L);
        stakeBody.put("gasFeeRq", 0.41666667);

        JSONObject jsonstakeObj = new JSONObject(stakeBody);

        // 打印请求开始时间
        System.out.println("Start Time: " + getCurrentTime());
        String resStake = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-stake/stake", jsonstakeObj.toString(), header);
        JSONObject jsonResStake = (JSONObject) JSONObject.parse(resStake);
        System.out.println("stake结果" + jsonResStake);
        return jsonResStake.toString();
    }


    public static String stakeMock(String clientId) throws Exception {
        // 设置请求头
        HashMap<String, String> header = getHeader();

        HashMap<String, Object> stakeBody = new HashMap<>();
        stakeBody.put("projectKey", "PT-STAKE");
        stakeBody.put("amountRq", "9500");
        stakeBody.put("expiryTime", 1687046400000L);
        stakeBody.put("gasFeeRq", 0.41666667);

        JSONObject jsonStakeObj = new JSONObject(stakeBody);

        // 打印请求开始时间
        System.out.println("Start Time: " + getCurrentTime());
        String url = "https://api10-fat2.phemex.com/phemex-stake/public/robot/stake?userId=" + clientId;
        String resStake = HttpClientUtil.jsonPost(url, jsonStakeObj.toString(), header);
        JSONObject jsonResStake = (JSONObject) JSONObject.parse(resStake);
        System.out.println("stakeMock结果: " + jsonResStake);
        return url + jsonResStake.toString();
    }


    static String redeemMock(String clientId) throws Exception {
        // 设置请求头
        HashMap<String, String> header = getHeader();

        HashMap<String, Object> redeemBody = new HashMap<>();
        redeemBody.put("projectKey", "PT-STAKE");

        JSONObject jsonRedeemObj = new JSONObject(redeemBody);
        String url = "https://api10-fat2.phemex.com/phemex-stake/public/robot/redeem?userId=" + clientId;
        String resRedeem = HttpClientUtil.jsonPost(url, jsonRedeemObj.toString(), header);
        JSONObject jsonResRedeem = (JSONObject) JSONObject.parse(resRedeem);
        System.out.println("redeem结果" + jsonResRedeem);
        return url + jsonResRedeem.toString();
    }

    static void redeem(HashMap<String, String> header, String responseHeader) throws Exception {
        header.put("phemex-auth-token", responseHeader);


        HashMap<String, Object> redeemBody = new HashMap<>();
        redeemBody.put("projectKey", "PT-STAKE");

        JSONObject jsonRedeemObj = new JSONObject(redeemBody);

        String resRedeem = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-stake/redeem", jsonRedeemObj.toString(), header);
        JSONObject jsonResRedeem = (JSONObject) JSONObject.parse(resRedeem);
        System.out.println("redeem结果" + jsonResRedeem);
    }

    private static void marginOpen(HashMap<String, String> header, String responseHeader) throws Exception {
        header.put("phemex-auth-token", responseHeader);

        String resMarginOpen= HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-account/accounts/margin/open", header);
        JSONObject jsonMarginOpen = (JSONObject) JSONObject.parse(resMarginOpen);
        System.out.println("MarginOpen结果：" + jsonMarginOpen);
    }
}
