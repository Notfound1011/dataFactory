package com.phemex.dataFactory.tools;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.tools.SqlGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */

import java.io.*;
import java.text.SimpleDateFormat;

public class SqlGen {

    public static void main(String[] args) throws Exception {
        String baseSql = "INSERT INTO `phemex_user`.`t_user_levels_%d` (`user_id`, `create_time`, `update_time`, `current_level`, `effective_level`, `proper_level`, `growth_value`, `validation_start`, `validation_end`, `origin_level_id`, `origin_level_type`, `hist_id`) VALUES (%d, '%s', '%s', 6, 6, 1, 60, '2022-05-12 00:00:00', '2022-08-10 00:00:00', 1524700455860555777, 1, 1524700455923470338);";
        String inputFilePath = "src/main/resources/input/userId.csv";
        String outputFilePath = "src/main/resources/output/test.sql";
        generateSql(baseSql, inputFilePath, outputFilePath);
    }

    /**
     * @Description: 读取inputFile中的uid，结合基础SQL语句baseSql，生成sql语句输出到outputFile
     * @Date: 2022/6/8
     * @Param baseSql: 基准sql语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateSql(String baseSql, String inputFilePath, String outputFilePath) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath)));//数据流读取文件
        StringBuffer strBuffer = new StringBuffer();
        long times = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = formatter.format(times);
        // UUID.randomUUID().toString().replaceAll("-", "");
        for (String str; (str = bufReader.readLine()) != null; ) {
            int uid = Integer.parseInt(str.split(",")[0]);
            int mod = uid % 10;  //对10取模，分表
            strBuffer.append(String.format(baseSql, mod, uid, currentTime, currentTime) + "\n");
        }

        bufReader.close();
        PrintWriter printWriter = new PrintWriter(outputFilePath);//替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * @Description: TODO
     * @Date: 2022/5/9
     * @Param outPutSqlPath:
     * @Param baseSql:
     * @Param sqlCount:
     **/
    public void outPutSql(String outPutSqlPath, String baseSql, int sqlCount) throws Exception {
        File file = new File(outPutSqlPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileWriter fileWriter = new FileWriter(file);
        for (int i = 0; i < sqlCount; i++) {
            fileWriter.write(baseSql + "\n");
            fileWriter.flush();
        }
        fileWriter.close();
        System.out.println("完成");
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

