package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.Map;
import java.util.Random;

import static com.phemex.dataFactory.common.utils.GoogleAuthenticator.generateTotp;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.LoadTestCommon
 * @Date: 2023年06月09日 14:57
 * @Description:
 */
public class LoadTestCommon {
    private static final Logger log = LoggerFactory.getLogger(LoadTestCommon.class);
    public static final String BID = "98620c53-c285-4dfc-4ac9-145d2d1a55f1";
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
            "2.0.0.0", "2.255.255.255", "46.0.0.0", "46.255.255.255"
    };

    public static String getRandomIp() {
        Random random = new Random();
        // Select either the first range (0) or the second range (1)
        int rangeIndex = random.nextInt(US_IP_RANGES.length / 2) * 2;
        String startIP = US_IP_RANGES[rangeIndex];
        String endIP = US_IP_RANGES[rangeIndex + 1];

        long startIPValue = ipToLong(startIP);
        long endIPValue = ipToLong(endIP);

        // Generate a random IP within the selected range
        long randomIPValue = startIPValue + (long) (random.nextDouble() * (endIPValue - startIPValue));
        return longToIP(randomIPValue);
    }

    private static long ipToLong(String ipAddress) {
        String[] ipAddressInArray = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < ipAddressInArray.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(ipAddressInArray[i]);
            result += ((long) ip) << (power * 8);
        }
        return result;
    }

    private static String longToIP(long ipNumber) {
        return ((ipNumber >> 24) & 0xFF) + "." +
                ((ipNumber >> 16) & 0xFF) + "." +
                ((ipNumber >> 8) & 0xFF) + "." +
                (ipNumber & 0xFF);
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
            JSONObject json_res = JSONObject.parseObject(res);
        }
        bufReader.close();
    }

    /*
     * @Description: 构建pubApi请求的headers
     * @Date: 2024/1/8
     * @Param path: 请求接口路径
     * @Param queryString: url后跟的参数
     * @Param body: body请求参数
     * @Param secretKey:
     * @Param accessToken:
     **/
    public static HashMap<String, String> headersPubApi(String path, String queryString, String body, String secretKey, String accessToken) {
        String expiry = ClientUtils.expiry();
        String signature = ClientUtils.sign(path, queryString, body, secretKey.getBytes());
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-phemex-request-expiry", expiry);
        headers.put("x-phemex-request-signature", signature);
        headers.put("x-phemex-access-token", accessToken);
        return headers;
    }


    public static String buildQueryString(Map<String, Object> params) {
        StringBuilder query = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                query.append("&");
            }
            query.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }

        return query.toString();
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
     * @Description: 免OTP登录获取token
     * @Date: 2022/12/30
     * @Param header: 请求头
     * @Param email: 登录用户名/邮箱
     * @Param password: 密码
     **/
    public static TokenInfo getTokenByLogin(HashMap<String, String> header, String email, String password) throws Exception {
        // 设置请求体
        Result result = getResult(header, email, password);
        String url = "https://api10-fat2.phemex.com/phemex-user/users/confirm/login" + "?code=" + result.code + "&mailCode=111111";
        CloseableHttpResponse res = HttpClientUtil.httpGet(url, header);
        System.out.println(EntityUtils.toString(res.getEntity()));
        String responseHeader = res.getFirstHeader("phemex-auth-token").getValue();

        return new TokenInfo(result.body, responseHeader);
    }

    /*
     * @Description: 通过OTP登录获取token
     * @Date: 2024/1/6
     * @Param header: 请求头
     * @Param email: 登录用户名/邮箱
     * @Param password: 密码
     * @Param totpSecret: OTP
     **/
    public static TokenInfo getTokenByLogin(HashMap<String, String> header, String email, String password, String totpSecret) throws Exception {
        Result result = getResult(header, email, password);
        String totpCode = generateTotp(totpSecret);

        String url = "https://api10-fat2.phemex.com/phemex-user/users/v2/confirm/login";
        HashMap<String, Object> body = new HashMap<>();
        body.put("code", result.code);
        body.put("otpCode", totpCode);
        CloseableHttpResponse res2 = HttpClientUtil.httpPost(url, body, header);
        String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();
        return new TokenInfo(result.body, responseHeader);
    }

    private static Result getResult(HashMap<String, String> header, String email, String password) throws Exception {
        // 设置请求体
        log.info(email + password + header.toString());
        HashMap<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("encryptVersion", 0);

        String res = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-user/users/login", body, header);
        JSONObject json_res = JSONObject.parseObject(res);
        String code = json_res.getJSONObject("data").getString("code");
        Result result = new Result(body, code);
        return result;
    }

    private static class Result {
        public final HashMap<String, Object> body;
        public final String code;

        public Result(HashMap<String, Object> body, String code) {
            this.body = body;
            this.code = code;
        }
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
//        transfer();
        System.out.println(getRandomIp());
    }
}
