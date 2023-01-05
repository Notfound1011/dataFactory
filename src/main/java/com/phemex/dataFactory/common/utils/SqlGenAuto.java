package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.SqlGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Map;

public class SqlGenAuto {

    public static void main(String[] args) throws Exception {
        String baseSql = "";
        String inputFilePath = "src/main/resources/input/cqSql.json";
        String outputFilePath = "src/main/resources/output/cqSql.sql";
        generateCqSql(inputFilePath, outputFilePath);
    }

    /**
     * @Description: 读取inputFile中的uid，结合基础SQL语句baseSql，生成sql语句输出到outputFile
     * @Date: 2022/6/8
     * @Param baseSql: 基准sql语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateCqSql(String inputFilePath, String outputFilePath) throws IOException {
        String json = inputFilePath;
        File jsonFile = new File(json);
        //通过上面那个方法获取json文件的内容
        String jsonData = getStr(jsonFile);
        //转json对象
        JSONObject parse = (JSONObject)JSONObject.parse(jsonData);
        //获取主要数据
        JSONArray data = parse.getJSONArray("data");
        //挨个遍历
        for (Object dt : data) {
            JSONObject dtObject =(JSONObject)dt;
            for (Map.Entry<String, Object> entry : dtObject.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().toString().split("\"value\":")[1].split("}")[0]);
            }

//            JSONObject user_id = dtObject.getJSONObject("user_id").getJSONObject("value");
//            System.out.println(id);

//            System.out.println(properties);
            //如果数据量大不建议这样入库 直接拼接sql 然后插入一次
        }
    }

    /**
     * @Description: TODO
     * @Date: 2022/5/9
     * @Param outPutSqlPath:
     * @Param baseSql:
     * @Param sqlCount:
     **/
    //把一个文件中的内容读取成一个String字符串
    public static String getStr(File jsonFile){
        String jsonStr = "";
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
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


    /**
     * @Description: TODO
     * @Date: 2022/5/9
     * @Param outPutSqlPath:
     * @Param baseSql:
     **/
    public void outPutSql(String outPutSqlPath, String baseSql) throws Exception {
        File file = new File(outPutSqlPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(baseSql + "\n");
        fileWriter.flush();
        fileWriter.close();
        System.out.println("完成");
    }

    /**
     * @Description: 读取文件
     * @Date: 2022/5/13
     * @Param baseFilePath:
     **/
    public String readSql(String baseFilePath) throws Exception {
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
}

