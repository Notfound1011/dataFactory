package com.phemex.dataFactory.common.utils;

import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.util.*;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.getHeader;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.tokenGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */
public class CopyTraderBatch {
    public static void main(String[] args) throws Exception {
        application();  // 成为copier、划转
//        copySettings();  // 跟单
    }

    /**
     * @Description: 注册成为copier, 并划转钱到ct账号
     * @Date: 2022/12/30
     **/
    private static void application() throws Exception {
        String baseFilePath = "src/main/resources/output/tokens.txt";

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)))) {
            bufReader.readLine();
            String str;
            while ((str = bufReader.readLine()) != null) {
                // 设置请求头
                HashMap<String, String> header = getHeader();
                header.put("phemex-auth-token", str);

                /*
                 * @Description: 成为copier，首次跟单时需要请求
                 * @Date: 2023/4/21
                 **/
//            String applicationBody = "{\"role\":\"Copier\"}";
//            String res = HttpClientUtil.jsonPost("https://api10-fat.phemex.com/phemex-lb/user/application", applicationBody, header);
//            JSONObject json_res = JSONObject.parseObject(res);
//            System.out.println(json_res);


                HashMap<String, Object> transferBody = new HashMap<>();
                transferBody.put("amountEv", "10000000000");
                transferBody.put("amount", "100.00000000");
                transferBody.put("currency", "USDT");
                transferBody.put("fromAccType", "SPOT");
                transferBody.put("toAccType", "CONTRACT");
//                transferBody.put("toAccType", "COPY_TRADE");

                String resTransfer = HttpClientUtil.jsonPost("https://api10-fat.phemex.com/exchanger-core/wallets/account/transfer", transferBody, header);
                JSONObject jsonResTransfer = JSONObject.parseObject(resTransfer);
                System.out.println(jsonResTransfer);
            }
        }
    }

    /**
     * @Description: 跟单设置
     * @Date: 2022/12/30
     **/
    private static void copySettings() throws Exception {
        String baseFilePath = "src/main/resources/output/tokens.txt";

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)))) {
            bufReader.readLine();
            String str;
            while ((str = bufReader.readLine()) != null) {
                // 设置请求头
                HashMap<String, String> header = getHeader();
                header.put("phemex-auth-token", str);

                String copySettingsRatio = "{\"traderId\":\"930217\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":44841,\"symbol\":\"LTCUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true},{\"code\":49641,\"symbol\":\"MASKUSDT\",\"selected\":true}],\"marginMode\":\"FOLLOW\",\"leverageType\":\"FOLLOW\",\"leverageRr\":0,\"amountType\":\"RATIO\",\"maxOpenAmountRv\":\"100000\",\"singleInvestRatio\":\"2\"}";
                String copySettingsFixed = "{\"traderId\":\"930217\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":44841,\"symbol\":\"LTCUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true},{\"code\":49641,\"symbol\":\"MASKUSDT\",\"selected\":true}],\"marginMode\":\"FOLLOW\",\"leverageType\":\"FOLLOW\",\"leverageRr\":0,\"amountType\":\"FIXED\",\"maxOpenAmountRv\":\"100000\",\"singleOpenAmountRv\":\"100\"}";
                String copySettings = Math.random() < 0.5 ? copySettingsRatio : copySettingsFixed;
                String resCopySettings = HttpClientUtil.
                        jsonPost("https://api10-fat.phemex.com/phemex-lb/copier/v3/setting", copySettings, header);
                JSONObject jsonResCopySettings = JSONObject.parseObject(resCopySettings);
                System.out.println(jsonResCopySettings);

                String copySettingsRatio2 = "{\"traderId\":\"930440\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true}],\"marginMode\":\"FOLLOW\",\"leverageType\":\"FOLLOW\",\"leverageRr\":0,\"amountType\":\"RATIO\",\"maxOpenAmountRv\":\"100422.2555\",\"singleInvestRatio\":\"2\"}";
                String copySettingsFixed2 = "{\"traderId\":\"930440\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true}],\"marginMode\":\"FOLLOW\",\"leverageType\":\"FOLLOW\",\"leverageRr\":0,\"amountType\":\"FIXED\",\"maxOpenAmountRv\":\"100422.2555\",\"singleOpenAmountRv\":\"100\"}";
                String copySettings2 = Math.random() < 0.5 ? copySettingsRatio2 : copySettingsFixed2;
                String resCopySettings2 = HttpClientUtil.
                        jsonPost("https://api10-fat.phemex.com/phemex-lb/copier/v3/setting", copySettings2, header);
                JSONObject jsonResCopySettings2 = JSONObject.parseObject(resCopySettings2);
                System.out.println(jsonResCopySettings2);
            }
        }
    }

    /**
     * @Description: 取消跟单
     * @Date: 2022/12/30
     **/
    private static void unCopy() throws Exception {
        String baseFilePath = "src/main/resources/output/tokens.txt";

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)))) {
            bufReader.readLine();
            String str;
            while ((str = bufReader.readLine()) != null) {
                // 设置请求头
                HashMap<String, String> header = getHeader();
                header.put("phemex-auth-token", str);

                String unCopyBody = "{\"traderId\":1005546}";
                String resUnCopy = HttpClientUtil.jsonPost("https://api10-fat.phemex.com/phemex-lb/user/uncopy", unCopyBody, header);
                JSONObject jsonResUnCopy = JSONObject.parseObject(resUnCopy);
                System.out.println(jsonResUnCopy);
            }
        }
    }
}
