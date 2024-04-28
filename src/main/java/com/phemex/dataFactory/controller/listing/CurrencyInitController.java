package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.CurrencyInitRequest;
import com.phemex.dataFactory.service.listing.CurrencyInitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.listing.CurrencyInitController
 * @Date: 2024年01月23日 17:54
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class CurrencyInitController {
    @Resource
    private CurrencyInitService currencyInitService;

    @ApiOperation(value = "currency初始化", notes = "", httpMethod = "POST")
    @PostMapping("/admin/api/currency-init")
    public ResultHolder openPositionPubApi(@RequestBody @Valid CurrencyInitRequest currencyInitRequest) throws Exception {
        return currencyInitService.run(currencyInitRequest);
    }
}
