package com.think.tcp.server.exception;

public class ThinkTcpTransferException extends RuntimeException{
    public ThinkTcpTransferException() {
        super("通信异常");
    }

    public ThinkTcpTransferException(String message) {
        super(message);
    }
}
