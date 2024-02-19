package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.MarketDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.listing.MarketDataController
 * @Date: 2024年02月18日 11:05
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class MarketDataController {
    @Resource
    private MarketDataService marketDataService;

    @ApiOperation(value = "校验trend", notes = "", httpMethod = "GET")
    @GetMapping("/check-trend")
    public ResultHolder checkTrend(@RequestParam List<String> symbolList,
                                   @RequestParam String env) {
        return marketDataService.getTrendData(symbolList, env);
    }

    @ApiOperation(value = "校验kline", notes = "", httpMethod = "GET")
    @GetMapping("/check-kline")
    public ResultHolder checkKline(@RequestParam List<String> symbolList,
                                   @RequestParam String env) {
        return marketDataService.getKlineData(symbolList, env);
    }
}
