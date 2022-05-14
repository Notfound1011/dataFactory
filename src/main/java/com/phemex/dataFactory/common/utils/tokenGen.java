package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.SqlGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;


import java.io.*;
import java.util.*;

public class tokenGen {

//    public static void main(String[] args) throws Exception {
//        generateToken();
//    }

    public void generateToken() throws Exception {
        String loadTestValue = ActionEncoderTool.loadTestValue("5ef9cfe9-ca67-412c7-9b9c-54a2b49ba35", 30);
        HashMap<String, String> header = new HashMap<>();
        header.put("x-load-test", loadTestValue);
        header.put("bid", "0ebf6c8e-29fe-b118-27fa-4f16014db580");

        String baseFilePath = "src/main/java/com/phemex/dataFactory/common/utils/output/user_token.csv";
        String outputFilePath = "src/main/java/com/phemex/dataFactory/common/utils/output/user_token_value.csv";

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(baseFilePath)));//数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("uid,email,token\n");
        for (String str; (str = bufReader.readLine()) != null; ) {
            HashMap<String, Object> body = new HashMap<>();
            body.put("email", str.split(",")[1]);
            body.put("password", "Shiyu@123");
            body.put("encryptVersion", 0);
            JSONObject jsonObj = new JSONObject(body);
            String res = HttpClientUtil.jsonPost("http://internal-alb-internal-sp-nginx-qa2-124215266.ap-southeast-1.elb.amazonaws.com/api/phemex-user/users/login", jsonObj.toString(), header);
            JSONObject json_res = (JSONObject) JSONObject.parse(res);
            String code = (String) json_res.getJSONObject("data").get("code");

            String url = "http://internal-alb-internal-sp-nginx-qa2-124215266.ap-southeast-1.elb.amazonaws.com/api/phemex-user/users/confirm/login" + "?code=" + code + "&mailCode=111111";
            CloseableHttpResponse res2 = HttpClientUtil.httpGet(url, header);
            String responseHeader = res2.getFirstHeader("phemex-auth-token").getValue();
            System.out.println(responseHeader);
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
     * @Description: 读取文件
     * @Date: 2022/5/13
     * @Param baseFilePath:
     **/
    public String readFile(String baseFilePath) throws Exception {
        File file = new File(baseFilePath);
        if (file.isFile()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            StringBuffer stringBuffer = new StringBuffer();
            int len = 0;
            while ((len = fileInputStream.read(buf)) != -1) {
                stringBuffer.append(new String(buf, 0, len, "GBK"));
            }
            return stringBuffer.toString();
        }
        return null;
    }


    /**
     * @Description: TODO
     * @Date: 2022/5/9
     * @Param outPutSqlPath:
     * @Param baseSql:
     **/
    public void outPutFile(String outPutFilePath, String baseValue) throws Exception {
        File file = new File(outPutFilePath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(baseValue);
        fileWriter.flush();
        fileWriter.close();
        System.out.println("完成");
    }
}

