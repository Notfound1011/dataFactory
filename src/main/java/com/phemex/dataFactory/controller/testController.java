package com.phemex.dataFactory.controller;

import com.phemex.dataFactory.common.utils.tokenGen;
import com.phemex.dataFactory.controller.request.ResultHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.controller.testController
 * @Date: 2022年05月14日 11:30
 * @Description:
 */
@RequestMapping("test")
@RestController
public class testController {
    @GetMapping("/generateToken")
    public String insertUsers() throws Exception {
        tokenGen tokenGen = new tokenGen();
        tokenGen.generateToken();
        return "success";
    }
}
