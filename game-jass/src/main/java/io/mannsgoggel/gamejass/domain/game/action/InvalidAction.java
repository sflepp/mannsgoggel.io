package io.mannsgoggel.gamejass.domain.game.action;

public class InvalidAction extends RuntimeException {
    public InvalidAction(String message) {
        super(message);
    }
}
