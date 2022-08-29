package com.caston.wechat.exception;

public class WeChatException extends RuntimeException {
    public WeChatException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public WeChatException(String s) {
        super(s);
    }
}
