package io.mannsgoggel.gamejass.domain.game.player;

import io.mannsgoggel.gamejass.domain.game.state.State;
import io.mannsgoggel.gamejass.domain.game.Store;
import lombok.Data;

import java.util.function.Consumer;


@Data
public abstract class Player implements Consumer<State> {
    private final String name;
    private Store store;
}