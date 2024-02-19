package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.FundingRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.listing.FundingRateController
 * @Date: 2024年01月05日 17:15
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class FundingRateController {
    @Resource
    private FundingRateService fundingRateService;

    @ApiOperation(value = "检查funding rate", notes = "", httpMethod = "GET")
    @GetMapping("/check-funding-rate")
    public ResultHolder checkFundingRate(
            @RequestParam String env,
            @RequestParam long timestamp,
            @RequestParam List<String> symbolList,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        return fundingRateService.checkFundingRate(env, timestamp, symbolList, limit);
    }
}
