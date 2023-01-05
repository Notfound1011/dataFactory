package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.LoadTestHelper
 * @Date: 2022年10月28日 11:55
 * @Description:
 */
import com.phemex.dataFactory.common.exception.CommonMessageCode;
import com.phemex.dataFactory.common.exception.PhemexException;
import com.phemex.dataFactory.common.utils.RawAesMessageEncryptor;
import com.phemex.dataFactory.common.utils.ActionEncoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnProperty(value = "phemex.features.api.verifycode", matchIfMissing = true)
public class LoadTestHelper {

    private final String loadTestStr;

    private final String loadTestStrV2;

    private final ActionEncoder actionEncoder;

    @Getter
    private final RawAesMessageEncryptor rawAesMessageEncryptor;

    public LoadTestHelper(@Value("${phemex.load.test:}") String loadTestStr,
                          @Value("${phemex.load.test.v2.str:}") String loadTestStrV2,
                          @Value("${phemex.load.test.v2.secret:}") String loadtestV2Secret,
                          ActionEncoder actionEncoder) {
        this.loadTestStr = loadTestStr;
        this.loadTestStrV2 = loadTestStrV2;
        this.rawAesMessageEncryptor = new RawAesMessageEncryptor(loadtestV2Secret);
        this.actionEncoder = actionEncoder;
    }

    public void verifyLoadTest(String encodedLoadTestStr) {
        if (StringUtils.isEmpty(encodedLoadTestStr)) {
            throw new PhemexException(CommonMessageCode.HUMAN_BEHAVIOR_CHECK_ERROR, "Load test value error, raw str:" + encodedLoadTestStr);
        }

        if (!verifyLoadTestV1(encodedLoadTestStr) && !verifyLoadTestV2(encodedLoadTestStr)) {
            throw new PhemexException(CommonMessageCode.HUMAN_BEHAVIOR_CHECK_ERROR, "Load test value error, raw str:" + encodedLoadTestStr);
        }
    }


    private boolean verifyLoadTestV1(String encodedLoadTestStr) {
        if (StringUtils.isEmpty(loadTestStr)) {
            return false;
        }
        String v;
        try {
            v = this.actionEncoder.base64Decode(encodedLoadTestStr, String.class);
        } catch (Exception ex) {
            log.warn("Failed to verify load test of v1", ex);
            return false;
        }

        return v != null && v.equals(loadTestStr);
    }

    private boolean verifyLoadTestV2(String encodedLoadTestStr) {
        if (StringUtils.isEmpty(loadTestStrV2)) {
            return false;
        }
        String v = null;
        try {
            v = this.rawAesMessageEncryptor.decode(encodedLoadTestStr);
        } catch (Exception ex) {
            log.warn("Failed to verify load test of v2", ex);
            return false;
        }

        return v != null && v.equals(loadTestStrV2);
    }

}