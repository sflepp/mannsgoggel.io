package io.mannsgoggel.tournamentserver.games.jass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class GameOptions {
    @JsonProperty("name")
    String name;
    @JsonProperty("filter")
    FilterType filter;

    public enum FilterType {
        ALL,
        PLAYER_ONLY
    }
}