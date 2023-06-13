package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
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
            "3.0.0.0", "3.255.255.255", "8.0.0.0", "8.255.255.255", "13.0.0.0", "13.255.255.255", "17.0.0.0", "17.255.255.255",
            "18.0.0.0", "18.255.255.255", "19.0.0.0", "19.255.255.255", "20.0.0.0", "20.255.255.255", "23.0.0.0", "23.255.255.255",
            "24.0.0.0", "24.255.255.255", "27.0.0.0", "27.255.255.255", "32.0.0.0", "32.255.255.255", "50.0.0.0", "50.255.255.255",
            "64.0.0.0", "64.255.255.255", "65.0.0.0", "65.255.255.255", "66.0.0.0", "66.255.255.255",
            "67.0.0.0", "67.255.255.255", "68.0.0.0", "68.255.255.255", "69.0.0.0", "69.255.255.255", "70.0.0.0", "70.255.255.255", "71.0.0.0", "71.255.255.255",
            "72.0.0.0", "72.255.255.255", "73.0.0.0", "73.255.255.255", "74.0.0.0", "74.255.255.255", "96.0.0.0", "96.255.255.255",
            "97.0.0.0", "97.255.255.255", "98.0.0.0", "98.255.255.255", "99.0.0.0", "99.255.255.255", "100.0.0.0", "100.255.255.255",
            "104.0.0.0", "104.255.255.255", "107.0.0.0", "107.255.255.255", "108.0.0.0", "108.255.255.255", "162.0.0.0", "162.255.255.255",
            "173.0.0.0", "173.255.255.255", "184.0.0.0", "184.255.255.255", "199.0.0.0", "199.255.255.255", "204.0.0.0", "204.255.255.255",
            "205.0.0.0", "205.255.255.255", "206.0.0.0", "206.255.255.255", "207.0.0.0", "207.255.255.255", "208.0.0.0", "208.255.255.255",
            "209.0.0.0", "209.255.255.255", "216.0.0.0", "216.255.255.255", "198.0.0.0", "198.255.255.255"
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
     * @Description: 给账号充值
     * @Date: 2022/12/30
     **/
    static void deposit(int amountRv, String currency) throws Exception {
        BigDecimal amountToDeposit = BigDecimal.valueOf(amountRv);
        // 参数校验
        if (amountToDeposit == null || amountToDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid amount to deposit: " + amountToDeposit);
        }
        // 设置读取文件的路径
        String baseFilePath = "src/main/resources/input/user_login_info.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, Object> body = new HashMap<>();
            HashMap<String, Object> urlParam = new HashMap<>();
            urlParam.put("userId", str.split(",")[0]);
            urlParam.put("currency", currency);
            BigDecimal amountEv = amountToDeposit.multiply(MULTIPLIER);
            urlParam.put("amountEv", amountEv);
            body.put("host", "https://fat.phemex.com");
            body.put("path", "/api/spot/public/wallet");
            body.put("urlParam", urlParam);

            JSONObject jsonObj = new JSONObject(body);
            System.out.println(jsonObj.toString());
            String res = HttpClientUtil.jsonPost("http://3.1.250.199:8084/wallet/deposit", jsonObj.toString());
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
            if (append) {
                // 向文件中追加内容
                Files.writeString(Paths.get(outputFilePath), content, StandardOpenOption.APPEND);
                System.out.println("内容已成功追加到文件。");
            } else {
                // 替换文件内容
                Files.writeString(Paths.get(outputFilePath), content);
            }
        } catch (IOException e) {
            System.out.println("写入文件时发生错误。");
            e.printStackTrace();
        }
    }

}
