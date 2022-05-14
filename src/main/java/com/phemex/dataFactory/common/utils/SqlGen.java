package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.SqlGen
 * @Date: 2022年05月09日 10:56
 * @Description:
 */

import java.io.*;

public class SqlGen {

    public static void main(String[] args) throws Exception {
        String baseSql = "INSERT INTO `phemex_privilege`.`marketing_privilege_7` (`id`, `privilege_id`, `create_time`, `update_time`, `uid`, `activity_id`, `template_id`, `qualification_id`, `name`, `body`, `status`, `reason`, `type`, `extra`, `start_time`, `end_time`, `reference_id`) " +
                "VALUES (%d, '%s', '2022-05-07 07:31:27.535', '2022-05-07 07:31:27.000', 200707, 1, 158, NULL, 'Zero Trading Fees for Web and App (Monthly limit:$5,000)', '{\\\"limit\\\":null,\\\"dayLimit\\\":null,\\\"monthLimit\\\":500000000000}', 3, NULL, 'SPOT_FREE_CARD', '{\\\"currentLevel\\\":\\\"3\\\",\\\"createTime\\\":\\\"1651908686079\\\",\\\"userLevelEventType\\\":\\\"MEMBERSHIP_ASSIGN\\\",\\\"beforeLevel\\\":\\\"1\\\"}', '2022-05-07 07:31:27.532', '2022-08-06 07:31:27.532', NULL);";

        File file = new File("./test.sql");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileWriter fileWriter = new FileWriter(file);
        for (int i = 0; i < 10; i++) {
            fileWriter.write(String.format(baseSql, i + 1, "sss") + "\n");
            fileWriter.flush();
        }
        fileWriter.close();
        System.out.println("完成");

//        UUID.randomUUID().toString().replaceAll("-", "");
//        SqlGen sqlGen = new SqlGen();
//        sqlGen.outPutSql("./test.sql", String.format(baseSql, "sss", 123, "aaa", "bbb", 456), 1);
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
}

