package com.gpc.exchange.exception;

import java.io.IOException;

public class InvalidDateFormatException extends IOException {

    public InvalidDateFormatException(String message) {
        super(message);
    }
}
