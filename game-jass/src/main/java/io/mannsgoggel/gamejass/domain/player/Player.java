package io.mannsgoggel.gamejass.domain.player;

import io.mannsgoggel.gamejass.domain.game.GameState;
import io.mannsgoggel.gamejass.domain.game.JassGame;
import lombok.Data;

import java.util.function.Consumer;


@Data
public abstract class Player implements Consumer<GameState> {
    private final String name;
    private JassGame game;
}