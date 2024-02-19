package com.phemex.dataFactory.controller.listing;

import com.phemex.dataFactory.request.ResultHolder;
import com.phemex.dataFactory.service.listing.IconCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Listing", description = "上币接口")
@RequestMapping("listing")
@RestController
public class IconCheckController {
    @Resource
    private IconCheckService iconCheckService;

    @Operation(summary = "检查icon上传", description = "检查icon上传", method = "POST")
    @GetMapping("/check-icons")
    public ResultHolder checkIcons(@RequestParam List<String> symbolList) throws Exception {
        return iconCheckService.run(symbolList);
    }
}
