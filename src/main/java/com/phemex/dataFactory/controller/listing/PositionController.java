package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.listing.PositionPubRequest;
import com.phemex.dataFactory.service.listing.PositionService;
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
 * @Package: com.phemex.dataFactory.controller.listing.PositionController
 * @Date: 2024年01月19日 18:15
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class PositionController {
    @Resource
    private PositionService positionService;

    @ApiOperation(value = "pub api开仓", notes = "", httpMethod = "POST")
    @PostMapping("/pub/api/open-position")
    public ResultHolder openPositionPubApi(@RequestBody @Valid PositionPubRequest positionPubRequest) throws Exception {
        return positionService.openPosition(positionPubRequest);
    }

    @ApiOperation(value = "pub api平仓", notes = "", httpMethod = "POST")
    @PostMapping("/pub/api/clearance")
    public ResultHolder clearancePubApi(@RequestBody @Valid PositionPubRequest positionPubRequest) throws Exception {
        return positionService.clearance(positionPubRequest);
    }
}
