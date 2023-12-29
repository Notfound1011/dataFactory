package com.phemex.dataFactory.service;

import com.phemex.dataFactory.request.ResultHolder;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.UmsMemberService
 * @Date: 2023年01月04日 10:41
 * @Description: 会员管理Service
 */
public interface UmsMemberService {
    /**
     * 生成验证码
     */
    ResultHolder generateAuthCode(String telephone);

    /**
     * 判断验证码和手机号码是否匹配
     */
    ResultHolder verifyAuthCode(String telephone, String authCode);
}
