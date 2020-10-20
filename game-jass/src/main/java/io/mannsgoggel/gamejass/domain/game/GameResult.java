package io.mannsgoggel.gamejass.domain.game;

import lombok.Value;

import java.util.List;

@Value
public class GameResult {
    List<Team> teams;
}
