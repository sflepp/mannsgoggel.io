package com.manoggeli.gamejass.domain.action;

public class InvalidAction extends RuntimeException {
    public InvalidAction(String message) {
        super(message);
    }
}
