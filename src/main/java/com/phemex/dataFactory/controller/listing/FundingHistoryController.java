package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.FundingHistoryPubRequest;
import com.phemex.dataFactory.request.listing.FundingHistoryRestRequest;
import com.phemex.dataFactory.service.listing.FundingHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.listing.FundingHistoryCheckController
 * @Date: 2024年01月06日 10:22
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class FundingHistoryController {
    @Resource
    private FundingHistoryService fundingHistoryService;

    @ApiOperation(value = "rest api检查funding history", notes = "", httpMethod = "POST")
    @PostMapping("/rest/api/check-funding-history")
    public ResultHolder checkFundingHistoryRestApi(@RequestBody @Valid FundingHistoryRestRequest fundingHistoryRestRequest) throws Exception {
        return fundingHistoryService.checkFundingByRestApi(fundingHistoryRestRequest);
    }

    @ApiOperation(value = "pub api检查funding history", notes = "", httpMethod = "POST")
    @PostMapping("/pub/api/check-funding-history")
    public ResultHolder checkFundingHistoryPubApi(@RequestBody @Valid FundingHistoryPubRequest fundingHistoryPubRequest) throws Exception {
        return fundingHistoryService.checkFundingByPubApi(fundingHistoryPubRequest);
    }
}
