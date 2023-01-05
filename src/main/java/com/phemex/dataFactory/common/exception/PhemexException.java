package com.phemex.dataFactory.common.exception;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.exception.PhemexException
 * @Date: 2022年10月28日 11:52
 * @Description:
 */
import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

public class PhemexException extends RuntimeException {

    @Getter
    private final PhemexMessageCode messageCode;

    public PhemexException(PhemexMessageCode messageCode, String desc, Exception ex) {
        super(desc, ex);
        this.messageCode = messageCode;
    }

    public PhemexException(PhemexMessageCode messageCode, String message) {
        super(message);
        this.messageCode = messageCode;
    }

    /**
     *
     * @param messageCode
     * @param msgFormat 与slf4j格式相同。eg. "user token expired, userId={}, token={}"
     * @param args
     */
    public PhemexException(PhemexMessageCode messageCode, String msgFormat, Object... args) {
        super(MessageFormatter.arrayFormat(msgFormat, args).getMessage());
        this.messageCode = messageCode;
    }
}
