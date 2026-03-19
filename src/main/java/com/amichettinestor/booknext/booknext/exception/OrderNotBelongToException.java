package com.amichettinestor.booknext.booknext.exception;

public class OrderNotBelongToException extends RuntimeException {
    public OrderNotBelongToException(String s) {
        super(s);
    }
}
