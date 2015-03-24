package com.pinorman.wxmon.message;

public class SerializeException extends Exception {

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
