package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.CoinPairsInitRequest;
import com.phemex.dataFactory.service.listing.CoinPairsInitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
public class CoinPairsInitController {
    @Resource
    private CoinPairsInitService coinPairsInitService;

    @ApiOperation(value = "pub api开仓", notes = "", httpMethod = "POST")
    @PostMapping("/admin/api/coin-pairs-init")
    public ResultHolder openPositionPubApi(@RequestBody @Valid CoinPairsInitRequest coinPairsInitRequest) {
        return coinPairsInitService.coinPairsInit(coinPairsInitRequest);
    }
}
