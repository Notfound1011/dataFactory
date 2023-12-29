package com.phemex.dataFactory.tools;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.tools.GenUserSqlGen
 * @Date: 2022年06月08日 10:56
 * @Description:
 */

import java.io.*;
import java.text.SimpleDateFormat;

public class GenUserSqlGen {

    public static void main(String[] args) throws Exception {
        String baseSql = "INSERT INTO `phemex_user`.`t_clients` (`client_id`, `email`, `client_cnt`, `phone`, `phone_verified`, `password`, `password_utime`, `password_fail_count`, `parent_id`, `remark`, `status`, `totp`, `totp_secret`, `nick_name`, `photo_uri`, `referral_id`, `logon_state`, `parent_email`, `recommend_type`, `client_type`, `preference_flags`, `region_code`, `referral_type`, `referral_code`, `agent_id`, `referral_type_utime`, `lang`, `member_level_code`, `currency`, `totp_utime`) VALUES ('%s', '%s', '0', NULL, '0', '$2a$06$iVfatFTYeOGsNLJR8ayJ6eIVXQbt17fIt/t2HLt.yDD4x9SjslYYe', now(), '0', '0', NULL, '1', '1', '4S6MKCZ6NG53AZHS', '%s', NULL, '0', '8389', NULL, 'None', NULL, NULL, '', '1', '', '0', now(), 'en', '2', 'USD', now());\n" +
                "INSERT INTO `phemex_user`.`t_client_roles`(`client_id`, `role`)VALUES('%s', '1');\n" +
                "INSERT INTO `phemex_user`.`t_client_roles`(`client_id`, `role`)VALUES('%s', '10');\n" +
                "INSERT INTO `phemex_user`.`t_client_roles`(`client_id`, `role`)VALUES('%s', '11');\n" +
                "INSERT INTO `phemex_user`.`t_client_roles`(`client_id`, `role`)VALUES('%s', '12');";
        String outputFilePath = "src/main/resources/output/test.sql";
        generateSql(baseSql, outputFilePath);
    }

    /**
     * @Description: 读取inputFile中的uid，结合基础SQL语句baseSql，生成sql语句输出到outputFile
     * @Date: 2022/6/8
     * @Param baseSql: 基准sql语句
     * @Param inputFilePath: 输入文件
     * @Param outputFilePath: 输出文件
     **/
    private static void generateSql(String baseSql, String outputFilePath) throws IOException {
        StringBuffer strBuffer = new StringBuffer();
        int clientIdInit = 1848613;
        String emailPrefix = "syy";
        String emailSuffix = "@yopmail.com";

        for (int i = 0; i < 500; i++) {
            // 循环体逻辑
            int clientId = clientIdInit + i;
            String email = emailPrefix + 20 + i + emailSuffix;
            String sql = String.format(baseSql, clientId, email, email, clientId, clientId , clientId,clientId );
            strBuffer.append(sql + "\n");
        }

        long times = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = formatter.format(times);

//        String sql = String.format(baseSql, mod, uid, currentTime, currentTime);

        PrintWriter printWriter = new PrintWriter(outputFilePath);//替换后输出的文件位置
        printWriter.write(strBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }
}

