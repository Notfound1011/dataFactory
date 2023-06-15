package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.ConcurrentRequests
 * @Date: 2023年06月09日 14:47
 * @Description:
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.phemex.dataFactory.common.utils.LoadTestCommon.*;
import static com.phemex.dataFactory.common.utils.LoadTestCommon.getTokenByLogin;
import static com.phemex.dataFactory.common.utils.PspMintBatch.*;


public class ConcurrentRequests {
    private static final int NUM_USERS = 50;
    private static final int CONCURRENT_ACCESS = 50;


    public static void main(String[] args) throws IOException {
        // 登录获取tokens: 读取email,执行并发登录获取token的请求
//        String inputFilePath = "src/main/resources/input/emails.txt";
//        String methodName = "getTokenByLogin";
//        String outputFilePath = "src/main/resources/output/tokens.txt";
//        executeConcurrentRequests(inputFilePath, methodName, outputFilePath);


        // stake: 读取tokens,执行并发stake请求
//        String inputFilePath = "src/main/resources/output/tokens.txt";
//        String methodName = "stake";
//        String outputFilePath = "src/main/resources/output/stakeResults.txt";
//        executeConcurrentRequests(inputFilePath, methodName, outputFilePath);


        // 登录并执行其他请求: 执行并发loginAndOthers请求
//        String inputFilePath = "src/main/resources/input/emails.txt";
//        String methodName = "loginAndOthers";
//        String outputFilePath = "src/main/resources/output/mintResults.txt";
//        executeConcurrentRequests(inputFilePath, methodName, outputFilePath);


        // stakeMock: 读取clientId,执行并发stakeMock请求
        String inputFilePath = "src/main/resources/input/clientId.txt";
        String methodName = "stakeMock";
        String outputFilePath = "src/main/resources/output/stakeMockResults.txt";
        executeConcurrentRequests(inputFilePath, methodName, outputFilePath);

    }

    private static void executeConcurrentRequests(String inputFilePath, String methodName, String outputFilePath) throws IOException {
        Set<String> params = readParamsFromFile(inputFilePath);

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(NUM_USERS);

        // 创建Semaphore对象，设置初始许可数为10，许可证数量表示同时允许访问共享资源的线程数。
        Semaphore semaphore = new Semaphore(CONCURRENT_ACCESS);

        // 创建文件写入器
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        List<String> paramList = new ArrayList<>(params);

        // 根据文件的行数进行循环
        int totalLines = paramList.size();
        for (int i = 0; i < totalLines; i++) {
            // 获取一个不重复的param
            String param = getRandomParam(paramList);
            // 提交并发请求任务给线程池
            executor.execute(new RequestTask(param, semaphore, writer, methodName));
        }

        // 关闭线程池
        executor.shutdown();

        // 等待所有任务完成
        while (!executor.isTerminated()) {
            Thread.yield();
        }

        // 关闭文件写入器
        writer.close();
    }

    public static Set<String> readParamsFromFile(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .collect(Collectors.toSet());
    }

    private static String getRandomParam(List<String> params) {
        // 随机选择一个param，并从集合中移除，以确保不重复
        int index = ThreadLocalRandom.current().nextInt(params.size());
        return params.remove(index);
    }
}

class RequestTask implements Runnable {
    private String param;
    private Semaphore semaphore;
    private BufferedWriter writer;
    private String methodName;

    public RequestTask(String param, Semaphore semaphore, BufferedWriter writer, String methodName) {
        this.param = param;
        this.semaphore = semaphore;
        this.writer = writer;
        this.methodName = methodName;
    }

    @Override
    public void run() {
        try {
            // 在每个请求之前获取许可
            semaphore.acquire();
//            // 打印请求开始时间
//            System.out.println("Start Time: " + getCurrentTime());

            // 调用相应的方法
            if (methodName.equals("getTokenByLogin")) {
                // 设置请求头
                HashMap<String, String> header = getHeader();
                TokenInfo tokenInfo = getTokenByLogin(header, param, "Shiyu@123");
                // 写入结果到文件
                String result = tokenInfo.getResponseHeader();
                writer.write(result + "\n");
                writer.flush();
            } else if (methodName.equals("stake")) {
                String result = stake(param);
                writer.write(result + "\n");
                writer.flush();
            } else if (methodName.equals("stakeMock")) {
                String result = stakeMock(param);
                writer.write(result + "\n");
                writer.flush();
            } else if (methodName.equals("loginAndOthers")) {
                loginAndOthers(param);
            }


            // 模拟请求的耗时
            Thread.sleep(100);

            // 打印请求结束时间
            System.out.println("End Time: " + getCurrentTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 释放许可
            semaphore.release();
        }
    }

    public static String getCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return currentTime.format(formatter);
    }
}

