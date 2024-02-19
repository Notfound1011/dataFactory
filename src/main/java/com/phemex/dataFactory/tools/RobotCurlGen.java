package com.phemex.dataFactory.tools;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.tools.SqlGen
 * @Date: 2022年05月09日 10:56
 * @Description: Robot Faker Deposit and Transfer curl命令生成
 */

import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RobotCurlGen {

    public static void main(String[] args) throws Exception {
        String fakerDepositCurl = "curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{\"amount\": \"100000000\", \"currency\": 3, \"operaBy\": \"tool-batch\", \"remark\": \"fd-%d-100000000\", \"reqKey\": \"%s-%d-USDT\", \"userId\": %d }' 'http://localhost:6066/phemex-admin/admin/deposits/spot/fakerDeposit'";
        String transferCurlPrefix = "curl --location --request POST 'http://localhost:6066/phemex-admin/admin/robot/margin/transfer' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data-raw '{\"robotMarginTransferVos\": ";
        String transferCurlSuffix = "}'";

        String inputFilePath = "src/main/resources/input/robot_uid.csv";
        String outputFilePath = "src/main/resources/output/curl.txt";
        generateFakerDepositCurl(fakerDepositCurl, inputFilePath, outputFilePath);
        generateTransferCurl(transferCurlPrefix, transferCurlSuffix, inputFilePath, outputFilePath);
    }

    /**
     * @Description: 读取inputFile中的uid，结合基础fakerDepositCurl，生成curl语句输出到outputFile
     * @Date: 2023/01/06
     * @Param fakerDepositCurl: 基准curl语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateFakerDepositCurl(String fakerDepositCurl, String inputFilePath, String outputFilePath) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath)));//数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        long times = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = formatter.format(times);
        System.out.println(currentTime);
        for (String str; (str = bufReader.readLine()) != null; ) {
            int uid = Integer.parseInt(str.split(",")[0]);
            System.out.println(uid);
            strBuffer.append(String.format(fakerDepositCurl, uid, currentTime, uid, uid) + "\n");
        }

        bufReader.close();
        PrintWriter printWriter = new PrintWriter(outputFilePath);//替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }


    /**
     * @Description: 读取inputFile中的uid，结合基础transferCurl，生成curl语句输出到outputFile
     * @Date: 2023/01/06
     * @Param transferCurl: 基准curl语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateTransferCurl(String transferCurlPrefix, String transferCurlSuffix, String inputFilePath, String outputFilePath) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath)));//数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        HashMap<String, Object> hp = new HashMap<>();
        List list = new ArrayList();
        for (String str; (str = bufReader.readLine()) != null; ) {
            String uid = str.split(",")[0];
            System.out.println(uid);
            //{"userId": %d,"moveOp": 2,"currency": "USDT","amount": "100000000"}
            hp.put("userId", uid);
            hp.put("moveOp", 2);
            hp.put("currency", "USDT");
            hp.put("amount", "100000000");
            list.add(new JSONObject(hp).toString());
        }

        strBuffer.append(transferCurlPrefix + list + transferCurlSuffix);

        bufReader.close();
        FileWriter fileWriter = new FileWriter(outputFilePath,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter,1024);
        bufferedWriter.write(strBuffer.toString().toCharArray());
        bufferedWriter.flush();
        bufferedWriter.close();

    }

    /**
     * @Description: TODO
     * @Date: 2022/5/9
     * @Param outPutSqlPath:
     * @Param baseSql:
     * @Param sqlCount:
     **/
    //把一个文件中的内容读取成一个String字符串
    public static String getStr(File jsonFile) {
        String jsonStr = "";
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

