package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.FundingRateCheckService
 * @Date: 2024年01月05日 17:17
 * @Description:
 */

@Service
public class FundingRateService {
    private static final Logger log = LoggerFactory.getLogger(FundingRateService.class);
    private final Map<String, String> phemexHostMap;

    public FundingRateService(Map<String, String> phemexHostMap) {
        this.phemexHostMap = phemexHostMap;
    }

    public ResultHolder checkFundingRate(String env, long timestamp, List<String> symbolList, int limit) {
        List<String> errorList = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        // 使用并行流并使用try-catch块来处理异常
        symbolList.parallelStream()
                .map(symbol -> "." + symbol + "FR8H")
                .forEach(item -> {
                    String path = "/phemex-user/public/cfg/fundingRateList";
                    String url = String.format("%s%s?limit=%d&offset=0&symbol=%s&withCount=true",
                            phemexHostMap.get(env.toLowerCase()), path, limit, item);
                    try {
                        String response = HttpClientUtil.get(url);
                        if (!response.contains(String.valueOf(timestamp))) {
                            errorList.add(item);
                        }
                    } catch (Exception e) {
                        errorList.add(item);
                        log.warn("An error occurred while fetching funding rate for " + item + ": " + e.getMessage());
                    }
                });

        if (!errorList.isEmpty()) {
            return ResultHolder.error("Validation Failed", "有Symbol未找到对应时间的FundingRate记录: " + String.join(",", errorList));
        } else {
            return ResultHolder.success("所有Symbol均找到了对应时间的FundingRate记录: " + String.join(",", symbolList));
        }
    }
}
