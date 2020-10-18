package io.mannsgoggel.gamejass.domain;

import io.mannsgoggel.gamejass.domain.game.GameMode;
import io.mannsgoggel.gamejass.domain.game.Player;

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
