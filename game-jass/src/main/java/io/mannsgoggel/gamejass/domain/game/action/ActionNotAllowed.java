package io.mannsgoggel.gamejass.domain.game.action;

public class ActionNotAllowed extends RuntimeException {
    public ActionNotAllowed(String message) {
        super(message);
    }
}
