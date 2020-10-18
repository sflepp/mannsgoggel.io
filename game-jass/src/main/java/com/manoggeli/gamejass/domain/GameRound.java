package com.manoggeli.gamejass.domain;

import com.manoggeli.gamejass.domain.game.GameMode;
import com.manoggeli.gamejass.domain.game.Player;

import java.util.List;

public class GameRound {

    private GameMode.PlayingMode playingMode;
    private List<Team> teams;

    public GameRound(GameMode.PlayingMode playingMode, List<Team> teams) {
        this.playingMode = playingMode;
        this.teams = teams;
    }

    public Player getCurrentPlayer() {
        return null;
    }
}
