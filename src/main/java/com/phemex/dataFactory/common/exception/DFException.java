package com.phemex.dataFactory.common.exception;

public class DFException extends RuntimeException {

    private DFException(String message) {
        super(message);
    }

    private DFException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new DFException(message);
    }

    public static DFException getException(String message) {
        throw new DFException(message);
    }

    public static void throwException(Throwable t) {
        throw new DFException(t);
    }
}
