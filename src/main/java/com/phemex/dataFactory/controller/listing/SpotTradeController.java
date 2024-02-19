package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.SpotTradePubRequest;
import com.phemex.dataFactory.service.listing.SpotTradeService;
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
 * @Package: com.phemex.dataFactory.controller.listing.SpotTradeController
 * @Date: 2024年02月18日 15:19
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class SpotTradeController {
    @Resource
    private SpotTradeService spotTradeService;

    @ApiOperation(value = "pub api下单", notes = "", httpMethod = "POST")
    @PostMapping("/pub/api/spot/orders")
    public ResultHolder openPositionPubApi(@RequestBody @Valid SpotTradePubRequest spotTradePubRequest) throws Exception {
        return spotTradeService.createOrder(spotTradePubRequest);
    }
}
