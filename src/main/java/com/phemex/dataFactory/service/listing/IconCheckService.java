package com.phemex.dataFactory.service.listing;

import com.phemex.dataFactory.common.utils.HttpClientUtil;
import com.phemex.dataFactory.request.ResultHolder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.listing.IconCheckService
 * @Date: 2024年01月05日 15:50
 * @Description:
 */
@Service
public class IconCheckService {
    private static final Logger log = LoggerFactory.getLogger(IconCheckService.class);

    public ResultHolder run(List<String> symbolList) {
        List<String> errorList = symbolList.parallelStream()
                .map(item -> {
                    // 删除末尾的 "USDT"
                    String symbol = item.split(" ")[0].split("USDT")[0];
                    // 检查是否以小写 's' 或 'u' 开头，后面跟着数字
                    if (symbol.matches("^[su]\\d+.*")) {
                        // 删除开头的 's' 或 'u' 和紧跟的数字
                        return symbol.replaceFirst("^[su]\\d+", "");
                    } else if (symbol.startsWith("s")) {
                        // 删除开头的 's'
                        return symbol.substring(1);
                    }
                    // 如果没有特殊前缀，则原样返回
                    return symbol;
                })
                .filter(symbol -> !isIconUploaded(symbol))
                .collect(Collectors.toList());

        return generateResultMessage(errorList);
    }

    private boolean isIconUploaded(String symbol) {
        String url = "https://static.phemex.com/s/web/assets/" + symbol + ".svg";
        try (CloseableHttpResponse response = HttpClientUtil.httpGet(url, null)) {
            return response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            log.error("Error checking icon for symbol: " + symbol, e);
            return false;
        }
    }

    private ResultHolder generateResultMessage(List<String> errorList) {
        if (errorList.isEmpty()) {
            return ResultHolder.success("所有symbol执行完毕，icon均已上传!!");
        } else {
            return ResultHolder.error("有未上传icon的symbol", errorList);
        }
    }
}