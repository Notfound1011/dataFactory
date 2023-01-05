package com.phemex.dataFactory.service.impl;

import com.phemex.dataFactory.controller.request.ResultHolder;
import com.phemex.dataFactory.service.RedisService;
import com.phemex.dataFactory.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.service.impl.UmsMemberServiceImpl
 * @Date: 2023年01月04日 10:44
 * @Description:
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private RedisService redisService;
    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.key.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Override
    public ResultHolder generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        //验证码绑定手机号并存储到redis
        redisService.set(REDIS_KEY_PREFIX_AUTH_CODE + telephone, sb.toString());
        redisService.expire(REDIS_KEY_PREFIX_AUTH_CODE + telephone, AUTH_CODE_EXPIRE_SECONDS);
        return ResultHolder.success(sb.toString());
    }

    //对输入的验证码进行校验
    @Override
    public ResultHolder verifyAuthCode(String telephone, String authCode) {
        if (!StringUtils.hasLength(authCode)) {
            return ResultHolder.error("请输入验证码", null);
        }
        String realAuthCode = redisService.get(REDIS_KEY_PREFIX_AUTH_CODE + telephone);
        boolean result = authCode.equals(realAuthCode);
        if (result) {
            return ResultHolder.success("验证码校验成功");
        } else {
            return ResultHolder.error("验证码不正确");
        }
    }
}
