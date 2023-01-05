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
import java.util.*;

public class CopyTraderBatch {
    public static final String BID = "8183e28d-7feb-9d7d-97f9-f941e8f2f1be";
    public static final String LOAD_TEST_STR_V1 = "5ef9cfe9-ca67-412c7-9b9c-54a2b49ba35";
    public static final String LOAD_TEST_STR_V2 = "YisL1YpNZU-fea-RfNESJTkK-rb49-ba35";
    public static final String SECRET = "SoV~Yf@-12fd2x!r[Nv]^s*b";


    public static void main(String[] args) throws Exception {
        ActionEncoder actionEncoder = ActionEncoder.getInstance();

        LoadTestHelper loadTestHelper = new LoadTestHelper(LOAD_TEST_STR_V1, LOAD_TEST_STR_V2, SECRET, actionEncoder);
        String loadTestValueV2 = loadTestHelper.getRawAesMessageEncryptor().encode(LOAD_TEST_STR_V2, 50000);
        String ip = getRandomIp();

        HashMap<String, String> header = new HashMap<>();
        header.put("x-load-test", loadTestValueV2);
        header.put("Content-Type", "application/json");
        header.put("bid", BID);
        header.put("X-Forwarded-For", ip);

//        registerUser(header);  //注册账号

        genTokenByLogin(header); //通过登录获取token
//        deposit("USD");  // 充值
//        application();  // 成为copier、划转
//        copySettings();  // 跟单
    }

    /**
     * @Description: csv的方式，通过登录获取token
     * @Date: 2022/12/30
     * @Param null:
     **/
    private static void genTokenByLogin(HashMap<String, String> header) throws Exception {
        String baseFilePath = "src/main/resources/input/user_login_info.csv";
        String outputFilePath = "src/main/resources/output/user_login_result.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath))); // 数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("uid,email,password,token\n");
        int num = 0;
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("email", str.split(",")[1]);
            body.put("password", str.split(",")[2]);
            body.put("encryptVersion", 0);
            JSONObject jsonObj = new JSONObject(body);

            String res = HttpClientUtil.jsonPost("https://fat.phemex.com/api/phemex-user/users/login", jsonObj.toString(), header);
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            System.out.println(json_res);
            String code = (String) json_res.getJSONObject("data").get("code");

            String url = "https://fat.phemex.com/api/phemex-user/users/confirm/login" + "?code=" + code + "&mailCode=111111";
            CloseableHttpResponse res2 = HttpClientUtil.httpGet(url, header);
            String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();
            System.out.println(num++ + ": " + responseHeader + body.get("email"));
            strBuffer.append(str).append(",").append(responseHeader);
            strBuffer.append("\n"); // 行与行之间的分割
        }

        bufReader.close();
        PrintWriter printWriter = new PrintWriter(outputFilePath); // 替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * @Description: csv的方式批量注册账号
     * @Date: 2022/12/30
     * @Param null:
     **/
    private static void registerUser(HashMap<String, String> header) throws Exception {
        String baseFilePath = "src/main/resources/input/user_register_info.csv";
        String outputFilePath = "src/main/resources/output/user_register_result.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("id,email,password,token\n");
        int num = 0;
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("email", str.split(",")[1]);
            body.put("password", str.split(",")[2]);
            body.put("encryptVersion", 0);
            body.put("referralCode", "");
            body.put("nickName", null);
            body.put("lang", "en");
            body.put("group", 0);
            JSONObject jsonObj = new JSONObject(body);
            Thread.sleep(2000);
            String res = HttpClientUtil.jsonPost("https://fat5.phemex.com/api/phemex-user/users/register", jsonObj.toString(), header);
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            System.out.println(json_res);
            String code = (String) json_res.getJSONObject("data").get("code");

            String url = "https://fat5.phemex.com/api/phemex-user/users/confirm/register" + "?code=" + code + "&mailCode=111111" + "&email=" + str.split(",")[1];
            CloseableHttpResponse res2 = HttpClientUtil.httpGet(url, header);
            String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();
            System.out.println(num++ + ": " + responseHeader + body.get("email"));
            strBuffer.append(str).append(",").append(responseHeader);
            strBuffer.append("\n");//行与行之间的分割
        }

        bufReader.close();
        PrintWriter printWriter = new PrintWriter(outputFilePath);//替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * @Description: 给账号充值
     * @Date: 2022/12/30
     **/
    private static void deposit(String currency) throws Exception {
        String baseFilePath = "src/main/resources/input/user_login_info.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, Object> body = new HashMap<>();
            HashMap<String, Object> urlParam = new HashMap<>();
            urlParam.put("userId", str.split(",")[0]);
            urlParam.put("currency", currency);
            urlParam.put("amountEv", "10000000000");
            body.put("host", "https://fat.phemex.com");
            body.put("path", "/api/spot/public/wallet");
            body.put("urlParam", urlParam);

            JSONObject jsonObj = new JSONObject(body);

            String res = HttpClientUtil.jsonPost("http://3.1.250.199:8084/wallet/deposit", jsonObj.toString());
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            System.out.println(json_res);
        }
        bufReader.close();
    }


    /**
     * @Description: 注册成为copier, 并划转钱到ct账号
     * @Date: 2022/12/30
     **/
    private static void application() throws Exception {
        String baseFilePath = "src/main/resources/output/user_login_result.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        bufReader.readLine();
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, String> header = new HashMap<>();
            header.put("phemex-auth-token", str.split(",")[3]);
            header.put("Content-Type", "application/json");
            header.put("bid", BID);

            String applicationBody = "{\"role\":\"Copier\"}";
            String res = HttpClientUtil.jsonPost("https://fat4.phemex.com/api/phemex-lb/user/application", applicationBody, header);
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            System.out.println(json_res);


            HashMap<String, Object> transferBody = new HashMap<>();
            transferBody.put("amount", "1000000");
            transferBody.put("amountEv", "10000000000");
            transferBody.put("currency", "USD");
            transferBody.put("fromAccType", "SPOT");
            transferBody.put("toAccType", "COPY_TRADE");

            JSONObject jsonObj = new JSONObject(transferBody);

            String resTransfer = HttpClientUtil.jsonPost("https://fat4.phemex.com/api/exchanger-core/wallets/account/transfer", jsonObj.toString(), header);
            JSONObject jsonResTransfer = (JSONObject) JSONObject.parse(resTransfer);
            System.out.println(jsonResTransfer);
        }
        bufReader.close();
    }

    /**
     * @Description: 跟单设置
     * @Date: 2022/12/30
     **/
    private static void copySettings() throws Exception {
        String baseFilePath = "src/main/resources/output/user_login_result.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        bufReader.readLine();
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, String> header = new HashMap<>();
            header.put("phemex-auth-token", str.split(",")[3]);
            header.put("Content-Type", "application/json");
            header.put("bid", BID);

            String copySettings = "{\"marginType\":\"Isolated\",\"symbols\":[{\"code\":1021,\"symbol\":\"uBTCUSD\",\"selected\":true},{\"code\":11,\"symbol\":\"ETHUSD\",\"selected\":true},{\"code\":171,\"symbol\":\"DOTUSD\",\"selected\":true},{\"code\":31,\"symbol\":\"LINKUSD\",\"selected\":true},{\"code\":71,\"symbol\":\"ADAUSD\",\"selected\":true},{\"code\":381,\"symbol\":\"SOLUSD\",\"selected\":true},{\"code\":181,\"symbol\":\"UNIUSD\",\"selected\":true},{\"code\":21,\"symbol\":\"XRPUSD\",\"selected\":true},{\"code\":51,\"symbol\":\"LTCUSD\",\"selected\":true},{\"code\":41,\"symbol\":\"XTZUSD\",\"selected\":true},{\"code\":201,\"symbol\":\"DOGEUSD\",\"selected\":true},{\"code\":9221,\"symbol\":\"BNBUSD\",\"selected\":true}],\"traderId\":\"930440\",\"leverageEr\":500000000,\"singleOpenAmountEv\":500000,\"maxOpenAmountEv\":30000000}";
            String resCopySettings = HttpClientUtil.
                    jsonPost("https://fat4.phemex.com/api/phemex-lb/copier/setting", copySettings, header);
            JSONObject jsonResCopySettings = (JSONObject) JSONObject.parse(resCopySettings);
            System.out.println(jsonResCopySettings);

            String copySettings2 = "{\"marginType\":\"Isolated\",\"symbols\":[{\"code\":1021,\"symbol\":\"uBTCUSD\",\"selected\":true},{\"code\":11,\"symbol\":\"ETHUSD\",\"selected\":true},{\"code\":171,\"symbol\":\"DOTUSD\",\"selected\":true},{\"code\":31,\"symbol\":\"LINKUSD\",\"selected\":true},{\"code\":71,\"symbol\":\"ADAUSD\",\"selected\":true},{\"code\":381,\"symbol\":\"SOLUSD\",\"selected\":true},{\"code\":181,\"symbol\":\"UNIUSD\",\"selected\":true},{\"code\":21,\"symbol\":\"XRPUSD\",\"selected\":true},{\"code\":51,\"symbol\":\"LTCUSD\",\"selected\":true},{\"code\":41,\"symbol\":\"XTZUSD\",\"selected\":true},{\"code\":201,\"symbol\":\"DOGEUSD\",\"selected\":true},{\"code\":9221,\"symbol\":\"BNBUSD\",\"selected\":true}],\"traderId\":\"930439\",\"leverageEr\":500000000,\"singleOpenAmountEv\":500000,\"maxOpenAmountEv\":30000000}";
            String resCopySettings2 = HttpClientUtil.
                    jsonPost("https://fat4.phemex.com/api/phemex-lb/copier/setting", copySettings2, header);
            JSONObject jsonResCopySettings2 = (JSONObject) JSONObject.parse(resCopySettings2);
            System.out.println(jsonResCopySettings2);
        }
        bufReader.close();
    }

    /**
     * @Description: 取消跟单
     * @Date: 2022/12/30
     **/
    private static void unCopy() throws Exception {
        String baseFilePath = "src/main/resources/output/user_login_result.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        bufReader.readLine();
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, String> header = new HashMap<>();
            header.put("phemex-auth-token", str.split(",")[3]);
            header.put("Content-Type", "application/json");
            header.put("bid", BID);

            String unCopyBody = "{\"traderId\":1005546}";
            String resUnCopy = HttpClientUtil.
                    jsonPost("https://fat4.phemex.com/api/phemex-lb/user/uncopy", unCopyBody, header);
            JSONObject jsonResUnCopy = (JSONObject) JSONObject.parse(resUnCopy);
            System.out.println(jsonResUnCopy);
        }
        bufReader.close();
    }

    public static String getRandomIp() {
        // 指定 IP 范围
        int[][] range = {
                {607649792, 608174079}, // 36.56.0.0-36.63.255.255
                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
        };
        Random random = new Random();
        int index = random.nextInt(10);
        String ip = num2ip(range[index][0] + random.nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    /**
     * 将十进制转换成IP地址
     */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        b[0] = (ip >> 24) & 0xff;
        b[1] = (ip >> 16) & 0xff;
        b[2] = (ip >> 8) & 0xff;
        b[3] = ip & 0xff;
        // 拼接 IP
        String x = b[0] + "." + b[1] + "." + b[2] + "." + b[3];
        return x;
    }

}

