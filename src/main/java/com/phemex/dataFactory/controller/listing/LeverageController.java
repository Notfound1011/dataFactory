package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.listing.LeveragePubRequest;
import com.phemex.dataFactory.request.listing.LeverageRestRequest;
import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.LeverageService;
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
@Api(tags = "Listing", description = "上币工具")
@RequestMapping("listing")
@RestController
public class LeverageController {
    @Resource
    private LeverageService leverageService;

    @ApiOperation(value = "rest api检查leverage", notes = "", httpMethod = "POST")
    @PostMapping("/rest/api/check-leverage")
    public ResultHolder checkLeverageRestApi(
            @RequestBody @Valid LeverageRestRequest leverageCheckRestRequest) throws Exception {
        return leverageService.runByRestApi(leverageCheckRestRequest);
    }

    @ApiOperation(value = "pub api检查leverage", notes = "", httpMethod = "POST")
    @PostMapping("/pub/api/check-leverage")
    public ResultHolder checkLeveragePubApi(
            @RequestBody @Valid LeveragePubRequest leverageCheckPubRequest) throws Exception {
        return leverageService.runByPubApi(leverageCheckPubRequest);
    }
}
