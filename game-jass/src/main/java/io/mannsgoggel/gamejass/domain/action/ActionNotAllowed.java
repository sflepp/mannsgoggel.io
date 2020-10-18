package io.mannsgoggel.gamejass.domain.action;

public class ActionNotAllowed extends RuntimeException {
    public ActionNotAllowed(String message) {
        super(message);
    }
}
