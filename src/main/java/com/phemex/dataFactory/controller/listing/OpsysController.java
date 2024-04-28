package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.request.base.PhemexAdminApi;
import com.phemex.dataFactory.request.listing.ListFlowRequest;
import com.phemex.dataFactory.request.listing.PushNacosRequest;
import com.phemex.dataFactory.service.listing.OpsysService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
public class OpsysController {
    @Resource
    private OpsysService opsysService;

    @ApiOperation(value = "推送fat环境nacos", notes = "", httpMethod = "POST")
    @PostMapping("/push-nacos")
    public ResultHolder pushFatNacos(@RequestBody @Valid PushNacosRequest pushNacosRequest) {
        return opsysService.pushNacos(pushNacosRequest);
    }


    @ApiOperation(value = "同步fat环境nacos", notes = "", httpMethod = "GET")
    @GetMapping("/sync-nacos")
    public ResultHolder syncFatNacos(@RequestParam List<String> envs) {
        return opsysService.syncFatNacos(envs);
    }

    @ApiOperation(value = "创建上币工单", notes = "", httpMethod = "POST")
    @PostMapping("/creat-list-flow")
    public ResultHolder createListFlow(@RequestBody @Valid ListFlowRequest listFlowRequest) {
        return opsysService.createListFlow(listFlowRequest);
    }

    @ApiOperation(value = "创建上币工单", notes = "", httpMethod = "POST")
    @PostMapping("/deploy-list-flow")
    public ResultHolder deployListFlow(@RequestBody @Valid ListFlowRequest listFlowRequest) {
        return opsysService.deployListFlow(listFlowRequest);
    }

    @ApiOperation(value = "刷新CDN", notes = "", httpMethod = "POST")
    @PostMapping("/refresh-cdn")
    public ResultHolder refreshCDN(@RequestBody @Valid PhemexAdminApi phemexAdminApi) {
        return opsysService.refreshCDN(phemexAdminApi);
    }

}
