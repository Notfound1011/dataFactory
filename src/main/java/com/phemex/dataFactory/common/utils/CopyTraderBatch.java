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
import java.math.BigDecimal;
import java.util.*;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.deposit;
import static com.phemex.dataFactory.common.utils.LoadTestCommon.getHeader;

public class CopyTraderBatch {
    public static final String BID = "8183e28d-7feb-9d7d-97f9-f941e8f2f1be";

    public static void main(String[] args) throws Exception {

        deposit(10000, "USDT");  // 充值
//        application();  // 成为copier、划转
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
//            String res = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-lb/user/application", applicationBody, header);
//            JSONObject json_res = (JSONObject) JSONObject.parse(res);
//            System.out.println(json_res);


                HashMap<String, Object> transferBody = new HashMap<>();
                transferBody.put("amountEv", "10000000000000");
                transferBody.put("amount", "100000");
                transferBody.put("currency", "USDT");
                transferBody.put("fromAccType", "SPOT");
                transferBody.put("toAccType", "COPY_TRADE");

                JSONObject jsonObj = new JSONObject(transferBody);

                String resTransfer = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/exchanger-core/wallets/account/transfer", jsonObj.toString(), header);
                JSONObject jsonResTransfer = (JSONObject) JSONObject.parse(resTransfer);
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

                String copySettings = "{\"marginType\":\"Isolated\",\"symbols\":[{\"code\":1021,\"symbol\":\"uBTCUSD\",\"selected\":true},{\"code\":181,\"symbol\":\"UNIUSD\",\"selected\":true},{\"code\":11,\"symbol\":\"ETHUSD\",\"selected\":true},{\"code\":21,\"symbol\":\"XRPUSD\",\"selected\":true},{\"code\":42121,\"symbol\":\"APTUSD\",\"selected\":true},{\"code\":51,\"symbol\":\"LTCUSD\",\"selected\":true},{\"code\":171,\"symbol\":\"DOTUSD\",\"selected\":true},{\"code\":31,\"symbol\":\"LINKUSD\",\"selected\":true},{\"code\":41,\"symbol\":\"XTZUSD\",\"selected\":true},{\"code\":71,\"symbol\":\"ADAUSD\",\"selected\":true},{\"code\":201,\"symbol\":\"DOGEUSD\",\"selected\":true},{\"code\":381,\"symbol\":\"SOLUSD\",\"selected\":true},{\"code\":9221,\"symbol\":\"BNBUSD\",\"selected\":true}],\"traderId\":\"926326\",\"leverageEr\":500000000,\"singleOpenAmountEv\":5000000,\"maxOpenAmountEv\":50000000}";
//            String copySettings = "{\"marginType\":\"Isolated\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":44841,\"symbol\":\"LTCUSDT\",\"selected\":true},{\"code\":49641,\"symbol\":\"MASKUSDT\",\"selected\":true},{\"code\":52441,\"symbol\":\"LDOUSDT\",\"selected\":true},{\"code\":61241,\"symbol\":\"STXUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":44641,\"symbol\":\"LINKUSDT\",\"selected\":true},{\"code\":60341,\"symbol\":\"BLURUSDT\",\"selected\":true},{\"code\":51041,\"symbol\":\"GRTUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true},{\"code\":45641,\"symbol\":\"BNBUSDT\",\"selected\":true},{\"code\":55241,\"symbol\":\"DYDXUSDT\",\"selected\":true},{\"code\":56341,\"symbol\":\"OPUSDT\",\"selected\":true},{\"code\":61141,\"symbol\":\"CFXUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true}],\"traderId\":\"930217\",\"leverageRr\":5,\"singleOpenAmountRv\":\"100\",\"maxOpenAmountRv\":\"10000\"}";
                String resCopySettings = HttpClientUtil.
                        jsonPost("https://api10-fat2.phemex.com/phemex-lb/copier/v3/setting", copySettings, header);
                JSONObject jsonResCopySettings = (JSONObject) JSONObject.parse(resCopySettings);
                System.out.println(jsonResCopySettings);

                String copySettings2 = "{\"marginType\":\"Isolated\",\"symbols\":[{\"code\":41541,\"symbol\":\"BTCUSDT\",\"selected\":true},{\"code\":41941,\"symbol\":\"SOLUSDT\",\"selected\":true},{\"code\":61141,\"symbol\":\"CFXUSDT\",\"selected\":true},{\"code\":56341,\"symbol\":\"OPUSDT\",\"selected\":true},{\"code\":55241,\"symbol\":\"DYDXUSDT\",\"selected\":true},{\"code\":51041,\"symbol\":\"GRTUSDT\",\"selected\":true},{\"code\":60341,\"symbol\":\"BLURUSDT\",\"selected\":true},{\"code\":45641,\"symbol\":\"BNBUSDT\",\"selected\":true},{\"code\":44641,\"symbol\":\"LINKUSDT\",\"selected\":true},{\"code\":61241,\"symbol\":\"STXUSDT\",\"selected\":true},{\"code\":52441,\"symbol\":\"LDOUSDT\",\"selected\":true},{\"code\":49641,\"symbol\":\"MASKUSDT\",\"selected\":true},{\"code\":44841,\"symbol\":\"LTCUSDT\",\"selected\":true},{\"code\":63841,\"symbol\":\"ARBUSDT\",\"selected\":true},{\"code\":41841,\"symbol\":\"ADAUSDT\",\"selected\":true},{\"code\":49441,\"symbol\":\"APTUSDT\",\"selected\":true},{\"code\":48741,\"symbol\":\"MATICUSDT\",\"selected\":true},{\"code\":41741,\"symbol\":\"XRPUSDT\",\"selected\":true},{\"code\":47441,\"symbol\":\"FTMUSDT\",\"selected\":true},{\"code\":41641,\"symbol\":\"ETHUSDT\",\"selected\":true},{\"code\":44941,\"symbol\":\"DOGEUSDT\",\"selected\":true}],\"traderId\":\"930440\",\"leverageRr\":5,\"singleOpenAmountRv\":\"50\",\"maxOpenAmountRv\":\"10000\"}";
                String resCopySettings2 = HttpClientUtil.
                        jsonPost("https://api10-fat2.phemex.com/phemex-lb/copier/v3/setting", copySettings2, header);
                JSONObject jsonResCopySettings2 = (JSONObject) JSONObject.parse(resCopySettings2);
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
                String resUnCopy = HttpClientUtil.jsonPost("https://api10-fat2.phemex.com/phemex-lb/user/uncopy", unCopyBody, header);
                JSONObject jsonResUnCopy = JSONObject.parseObject(resUnCopy);
                System.out.println(jsonResUnCopy);
            }
        }
    }
}
