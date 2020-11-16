package io.mannsgoggel.gamejass.domain.game.state;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.mannsgoggel.gamejass.domain.game.GameModes;
import io.mannsgoggel.gamejass.domain.game.action.Actions;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class State {
    private final String playerName;
    private final Integer revision;
    private final Actions.ActionType nextAction;
    private final String nextPlayer;
    private final GameModes.GameMode.PlayingMode playingMode;
    private final Boolean shifted;
    private final List<Team> teams;
    private final List<CardState> cards;

    public static State initialState() {
        return new StateBuilder()
                .revision(0)
                .nextAction(Actions.ActionType.START_GAME)
                .cards(CardDeck.buildInitial())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        Color color;
        Suit suit;

        public enum Color {HEARTS, SPADES, DIAMONDS, CLUBS}

        public enum Suit {ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX}
    }

    @Data
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CardState {
        Card card;
        String player;
        Integer playOrder;
        String team;
        Boolean isTrump;
        Integer points;

        public boolean queryIsOnPlayer() {
            return playOrder == null && team == null;
        }

        public boolean queryIsOnTable() {
            return playOrder != null && team == null;
        }
    }

    @Value
    @Builder(toBuilder = true)
    public static class Team {
        String name;
        List<String> players;
        Integer points;
    }
}
