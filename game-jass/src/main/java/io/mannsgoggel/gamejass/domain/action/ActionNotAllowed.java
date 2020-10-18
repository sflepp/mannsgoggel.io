package com.manoggeli.gamejass.domain.action;

public class ActionNotAllowed extends RuntimeException {
    public ActionNotAllowed(String message) {
        super(message);
    }
}
