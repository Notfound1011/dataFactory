package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.NacosSyncService;
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
 * @Package: com.phemex.dataFactory.controller.listing.IconCheckController
 * @Date: 2024年01月05日 15:48
 * @Description:
 */
@Api(tags = "Listing")
@RequestMapping("listing")
@RestController
public class NacosSyncController {
    @Resource
    private NacosSyncService nacosSyncService;

    @ApiOperation(value = "同步fat环境nacos", notes = "", httpMethod = "GET")
    @GetMapping("/sync-nacos")
    public ResultHolder syncFatNacos(@RequestParam List<String> envs) {
        return nacosSyncService.syncFatNacos(envs);
    }
}
