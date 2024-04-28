package com.phemex.dataFactory.service.listing;

import com.alibaba.fastjson2.JSONObject;
import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.model.AdminAccount;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.CurrencyInfo;
import com.phemex.dataFactory.request.listing.CurrencyInitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.CurrencyInitService
 * @Date: 2024年01月22日 16:59
 * @Description:
 */
@Service
public class CurrencyInitService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyInitService.class);
    private final Map<String, String> phemexManageHostMap;
    private final TokenService tokenService;
    private final OpsysService opsysService;
    private final AdminAccountService adminAccountService;

    public CurrencyInitService(Map<String, String> phemexManageHostMap, TokenService tokenService, OpsysService opsysService, AdminAccountService adminAccountService) {
        this.phemexManageHostMap = phemexManageHostMap;
        this.tokenService = tokenService;
        this.opsysService = opsysService;
        this.adminAccountService = adminAccountService;
    }

    public ResultHolder run(CurrencyInitRequest currencyInitRequest) {
        AdminAccount mgmtAccount = adminAccountService.getAdminAccount(currencyInitRequest.getMgmtAccount().getOwner(), currencyInitRequest.getMgmtAccount().getAccountType());
        String env = currencyInitRequest.getEnv().toLowerCase();

        String host = phemexManageHostMap.get(env);
        String path = "/phemex-admin/phemex-common-service/admin/coin/basic";
        String url = host + path;
        if (mgmtAccount.getUsername() == null || mgmtAccount.getUsername().isEmpty() ||
                mgmtAccount.getPassword() == null || mgmtAccount.getPassword().isEmpty()) {
            return ResultHolder.error("初始化失败，请先到账号设置 设置管理后台账号！！！");
        }
        Map<String, String> header = tokenService.getManageToken(host, currencyInitRequest.getMgmtAccount());

        List<String> successCurrencies = new ArrayList<>();
        List<String> failedCurrencies = new ArrayList<>();
        boolean allSuccess = true;

        // 创建一个线程池
        ExecutorService executor = Executors.newFixedThreadPool(currencyInitRequest.getCurrencies().size());
        // 创建一个Future列表，用于存放任务的返回结果
        List<Future<ResultHolder>> futures = new ArrayList<>();

        for (CurrencyInfo currencyInfo : currencyInitRequest.getCurrencies()) {
            // 创建一个任务
            Callable<ResultHolder> task = () -> {
                if (currencyInfo.getTagOrMemo() == "memo") {
                    currencyInfo.setAddrExtra(1);
                } else if (currencyInfo.getTagOrMemo() == "tag") {
                    currencyInfo.setAddrExtra(2);
                } else {
                    currencyInfo.setAddrExtra(0);
                }
                try {
                    Map<String, Object> query = new HashMap<>();
                    query.put("currency", currencyInfo.getCurrency());
                    query.put("chain", currencyInfo.getChain());
                    String data = HttpClientUtil.get(url, header, query);
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    Long withdrawFixFeeEv = jsonObject.getJSONObject("data").getLong("withdrawFixFeeEv");
                    Long withdrawMinAmountEv = jsonObject.getJSONObject("data").getLong("withdrawMinAmountEv");
                    currencyInfo.setWithdrawFixFeeEv(withdrawFixFeeEv);
                    currencyInfo.setWithdrawMinAmountEv(withdrawMinAmountEv);
                    Map<String, Object> currencyMap = currencyInfo.toMap();

                    log.info("Currency content：{}", currencyMap);

                    HttpClientUtil.jsonPost(url, currencyMap, header);
                    // 如果初始化成功，则返回成功的ResultHolder
                    return ResultHolder.success("Currency initialized successfully: " + currencyInfo.getCurrency());
                } catch (Exception e) {
                    // 如果发生异常，则返回错误的ResultHolder
                    return ResultHolder.error("Failed to initialize currency: " + currencyInfo.getCurrency());
                }
            };
            // 提交任务到线程池执行
            futures.add(executor.submit(task));
        }

        // 关闭线程池，不再接受新的任务，但已提交的任务会继续执行
        executor.shutdown();

        // 处理并发任务的结果
        for (Future<ResultHolder> future : futures) {
            try {
                ResultHolder result = future.get(); // 获取任务的结果
                if (!result.isSuccess()) {
                    allSuccess = false;
                    failedCurrencies.add(result.getMessage());
                } else {
                    successCurrencies.add((String) result.getData());
                }
            } catch (Exception e) {
                allSuccess = false;
                log.error("Exception occurred during currency initialization.", e);
            }
        }

        ResultHolder refreshCDN = opsysService.refreshCDN(currencyInitRequest.getLdapAccount());
        if (refreshCDN != null) return refreshCDN;

        // 在所有任务完成后，根据结果决定返回值
        if (allSuccess) {
            String successMessage = "All currencies initialized successfully";
            return ResultHolder.success(successMessage, successCurrencies);
        } else {
            String errorMessage = "Some currencies initialization failed";
            return ResultHolder.error(errorMessage, failedCurrencies);
        }
    }
}
