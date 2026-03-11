package com.amichettinestor.booknext.booknext.exception;

public class PasswordsDontMatchException extends RuntimeException {
    public PasswordsDontMatchException(String mesagge) {
        super(mesagge);
    }
}
