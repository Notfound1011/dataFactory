package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Random;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.LoadTestCommon
 * @Date: 2023年06月09日 14:57
 * @Description:
 */
public class LoadTestCommon {
    public static final String BID = "c083fccb-ee0d-88e9-b344-cf1ae6623875";
    public static final String LOAD_TEST_STR_V1 = "5ef9cfe9-ca67-412c7-9b9c-54a2b49ba35";
    public static final String LOAD_TEST_STR_V2 = "YisL1YpNZU-fea-RfNESJTkK-rb49-ba35";
    public static final String SECRET = "SoV~Yf@-12fd2x!r[Nv]^s*b";
    private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(100000000);

    /**
     * @Description: 构建压测header
     * @author: yuyu.shi
     * @Date: 2023/6/8
     **/
    public static HashMap<String, String> getHeader() {
        ActionEncoder actionEncoder = ActionEncoder.getInstance();
        LoadTestHelper loadTestHelper = new LoadTestHelper(LOAD_TEST_STR_V1, LOAD_TEST_STR_V2, SECRET, actionEncoder);
        String loadTestValueV2 = loadTestHelper.getRawAesMessageEncryptor().encode(LOAD_TEST_STR_V2, 50000);
        String ip = getRandomIp();

        HashMap<String, String> header = new HashMap<>();
        header.put("x-load-test", loadTestValueV2);
        header.put("Content-Type", "application/json");
        header.put("bid", BID);
        header.put("X-Forwarded-For", ip);
        return header;
    }

    private static final String[] US_IP_RANGES = {
            "23.95.40.0", "23.95.43.255", "137.83.88.0", "137.83.89.255"
    };

    public static String getRandomIp() {
        Random random = new Random();
        int rangeIndex = random.nextInt(US_IP_RANGES.length / 2); // Select a random range
        String startIP = US_IP_RANGES[rangeIndex * 2];
        String endIP = US_IP_RANGES[rangeIndex * 2 + 1];

        long startIPValue = ipToLong(startIP);
        long endIPValue = ipToLong(endIP);

        long randomIPValue = startIPValue + random.nextLong() % (endIPValue - startIPValue + 1);
        return longToIP(randomIPValue);
    }

    private static long ipToLong(String ipAddress) {
        String[] ipAddressParts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            long octet = Long.parseLong(ipAddressParts[i]);
            result += octet << (24 - (8 * i));
        }
        return result;
    }

    private static String longToIP(long ip) {
        StringBuilder ipAddress = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            long octet = (ip >> (i * 8)) & 0xFF;
            ipAddress.append(octet);
            if (i > 0) {
                ipAddress.append(".");
            }
        }
        return ipAddress.toString();
    }

    /**
     * @Description: 划转
     * @Date: 2022/12/30
     **/
    static void transfer() throws Exception {

        // 设置读取文件的路径
        String baseFilePath = "src/main/resources/output/secretKey.csv";
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件

        String path = "/assets/transfer";
        String queryString = "";
        String body = "{\"amountEv\": 1000000000000,\"currency\": \"USDT\",\"moveOp\": 2}";

        for (String str; (str = bufReader.readLine()) != null; ) {
            String secretKey = str.split(",")[2];

            String signature = ClientUtils.sign(path, queryString, body, secretKey.getBytes());
            String expiry = ClientUtils.expiry();
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-phemex-request-signature", signature);
            headers.put("x-phemex-request-expiry", expiry);
            headers.put("x-phemex-access-token", str.split(",")[1]);
            String res = HttpClientUtil.jsonPost("https://fat-vapi.phemex.com" + path, body, headers);
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            System.out.println(json_res);
        }
        bufReader.close();
    }

    /**
     * @Description: 文件写入，支持覆盖和追加
     * @Date: 2023/6/9
     * @Param outputFilePath: 输出路径
     * @Param content: 写入内容
     * @Param append: true or false (是否追加)
     **/
    public static void writeToFile(String outputFilePath, String content, Boolean append) {
        try {
            Path path = Paths.get(outputFilePath);
            if (append) {
                // 向文件中追加内容
                Files.writeString(path, content, StandardOpenOption.APPEND);
                System.out.println("内容已成功追加到文件。");
            } else {
                // 替换文件内容
                Files.writeString(path, content);
            }
        } catch (IOException e) {
            System.out.println("写入文件时发生错误。");
            e.printStackTrace();
        }
    }

    /**
     * @Description: 通过登录获取token
     * @Date: 2022/12/30
     * @Param header: 请求头
     * @Param email: 登录用户名/邮箱
     * @Param password: 密码
     **/
    public static TokenInfo getTokenByLogin(HashMap<String, String> header, String email, String password) throws Exception {
        // 设置请求体
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("encryptVersion", 0);
        JSONObject jsonObj = new JSONObject(body);

        String res = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-user/users/login", jsonObj.toString(), header);
        JSONObject json_res = (JSONObject) JSONObject.parse(res);
        System.out.println(json_res);

        String code = (String) json_res.getJSONObject("data").get("code");
        String url = "https://api10-fat2.phemex.com/phemex-user/users/confirm/login" + "?code=" + code + "&mailCode=111111";
        CloseableHttpResponse res2 = HttpClientUtil.httpGet(url, header);
        String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();

        return new TokenInfo(body, responseHeader);
    }

    public static class TokenInfo {
        final HashMap<String, Object> body;
        final String responseHeader;

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

    public static void main(String[] args) throws Exception {
        transfer();
    }
}
