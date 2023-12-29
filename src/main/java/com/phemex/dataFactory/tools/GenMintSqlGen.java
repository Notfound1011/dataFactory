package com.phemex.dataFactory.tools;

import java.io.*;
import java.security.SecureRandom;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.tools.GenUserSqlGen
 * @Date: 2022年06月08日 10:56
 * @Description:
 */
public class GenMintSqlGen {

    public static void main(String[] args) throws Exception {
        String baseSql = "INSERT INTO phemex_user.t_sbt_soul_pass (`client_id`,`address`,`address_source`,`wallet_name`,`platform`,`chain_name`,`status`,`soul_pass_id`,`chain_tx_hash`,`chain_status`,`request_time`,`mint_time`,`minted_count`,`revoked_count`,`create_time`,`update_time`) VALUES ( %d, '%s', 'Phemex', NULL, 'WEB', 'ethereum', 3, %d, '0x%s', 'Done', '2023-04-07 08:07:04.909', '2023-04-07 08:07:24.0', 1, 0, '2023-04-07 08:07:04.862', '2023-04-07 08:07:30.102');";
        String inputFilePath = "src/main/resources/input/address.txt";
        String outputFilePath = "src/main/resources/output/test.sql";
        generateSql(inputFilePath, baseSql, outputFilePath);
    }

    /**
     * @Description: 读取inputFile中的uid，结合基础SQL语句baseSql，生成sql语句输出到outputFile
     * @Date: 2022/6/8
     * @Param baseSql: 基准sql语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateSql(String inputFilePath, String baseSql, String outputFilePath) throws IOException {
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath)));//数据流读取文件
        StringBuilder strBuffer = new StringBuilder();
        int lineNumber = 1368;

        for (String str; (str = bufReader.readLine()) != null; ) {
            int clientId = Integer.parseInt(str.split(",")[1]);
            String address = str.split(",")[0];

            lineNumber++;
            // 创建一个 SecureRandom 实例
            SecureRandom secureRandom = new SecureRandom();

            // 生成随机字节数组
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : randomBytes) {
                sb.append(String.format("%02x", b));
            }
            String randomHex = sb.toString();

            // 输出随机字符串
            System.out.println(randomHex);


            strBuffer.append(String.format(baseSql, clientId, address, lineNumber, randomHex)).append("\n");
        }

        bufReader.close();
        PrintWriter printWriter = new PrintWriter(outputFilePath);//替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }
}

