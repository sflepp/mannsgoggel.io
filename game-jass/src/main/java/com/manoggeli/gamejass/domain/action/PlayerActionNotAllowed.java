package com.manoggeli.gamejass.domain.action;

public class PlayerActionNotAllowed extends RuntimeException {
    public PlayerActionNotAllowed(String message) {
        super(message);
    }
}
