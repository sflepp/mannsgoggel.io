package com.manoggeli.gamejass.domain;

import com.manoggeli.gamejass.domain.gameObjects.Player;

import java.util.List;

public class GameRound {
    public enum PlayingMode {
        TOP_DOWN, BOTTOM_UP, TRUMP_HEARTHS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS
    }

    private PlayingMode playingMode;
    private List<Team> teams;

    public GameRound(PlayingMode playingMode, List<Team> teams) {
        this.playingMode = playingMode;
        this.teams = teams;
    }

    public Player getCurrentPlayer() {
        return null;
    }
}
