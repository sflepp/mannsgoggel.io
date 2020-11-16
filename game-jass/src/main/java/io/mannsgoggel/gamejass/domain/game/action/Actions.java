package io.mannsgoggel.gamejass.domain.game.action;

import io.mannsgoggel.gamejass.domain.game.state.*;
import io.mannsgoggel.gamejass.domain.game.GameModes;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.map;
import static io.mannsgoggel.gamejass.domain.game.JassRules.*;
import static io.mannsgoggel.gamejass.domain.game.action.Actions.ActionType.*;
import static io.mannsgoggel.gamejass.domain.game.state.Selectors.*;

public class Actions {
    public enum ActionType {
        START_GAME,
        START_ROUND,
        HAND_OUT_CARDS,
        SET_STARTING_PLAYER,
        DECIDE_SHIFT,
        SET_PLAYING_MODE,
        START_STICH,
        PLAY_CARD,
        END_STICH,
        END_ROUND,
        END_GAME,
        EXIT
    }

    public interface Action<T> extends Serializable {
        String getPlayer();
        ActionType getAction();
        T getPayload();

        State.StateBuilder reduce(State state);

        @Data
        abstract class BaseAction<T> implements Action<T> {
            String player;
            ActionType action;
            T payload;

            public BaseAction(ActionType action, String player, T payload) {
                this.action = action;
                this.player = player;
                this.payload = payload;
            }
        }
    }

    public static class StartGame extends Action.BaseAction<List<State.Team>> {
        public StartGame(List<State.Team> payload) {
            super(START_GAME, null, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder()
                    .teams(getPayload())
                    .nextAction(START_ROUND);
        }
    }

    public static class StartRound extends Action.BaseAction<Void> {
        public StartRound() {
            super(START_ROUND, null, null);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder().nextAction(HAND_OUT_CARDS);

        }
    }

    public static class HandOutCards extends Action.BaseAction<List<State.CardState>> {
        public HandOutCards(List<State.CardState> payload) {
            super(HAND_OUT_CARDS, null, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder()
                    .cards(getPayload())
                    .nextAction(SET_STARTING_PLAYER);
        }
    }

    public static class SetStartingPlayer extends Action.BaseAction<String> {
        public SetStartingPlayer(String payload) {
            super(SET_STARTING_PLAYER, null, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder()
                    .nextPlayer(getPayload())
                    .nextAction(DECIDE_SHIFT);
        }
    }

    public static class DecideShift extends Action.BaseAction<Boolean> {
        public DecideShift(String player, Boolean payload) {
            super(DECIDE_SHIFT, player, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            var shift = getPayload();
            var newState = state.toBuilder()
                    .shifted(shift)
                    .nextAction(SET_PLAYING_MODE);

            if (shift) {
                var teamMate = teamMateFor(state, state.getNextPlayer());
                newState.nextPlayer(teamMate);
            }

            return newState;
        }
    }

    public static class SetPlayingMode extends Action.BaseAction<GameModes.GameMode.PlayingMode> {
        public SetPlayingMode(String player, GameModes.GameMode.PlayingMode payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            var playingMode = getPayload();

            var newState = state.toBuilder()
                    .playingMode(playingMode);


            if (state.getShifted()) {
                var teamMate = teamMateFor(state, state.getNextPlayer());
                newState.nextPlayer(teamMate);
            }

            return newState.nextAction(START_STICH);
        }
    }

    public static class StartStich extends PlayCard {
        public StartStich(String player, State.Card payload) {
            super(START_STICH, player, payload);
        }
    }

    public static class PlayCard extends Action.BaseAction<State.Card> {
        public PlayCard(ActionType action, String player, State.Card payload) {
            super(action, player, payload);
        }

        public PlayCard(String player, State.Card payload) {
            super(PLAY_CARD, player, payload);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            var card = getPayload();
            var mode = state.getPlayingMode();
            var tableStack = tableStackCards(state);
            var playerCards = handCards(state, getPlayer());


            if (!playableCards(mode, playerCards, tableStack).contains(card)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            var intermediate = state.toBuilder()
                    .cards(map(state.getCards(), c -> c.getCard().equals(card) ? c.toBuilder().playOrder(playedCards(state).size()).build() : c))
                    .build();

            return intermediate
                    .toBuilder()
                    .nextPlayer(stichFinished(intermediate) ? null : nextPlayer(getPlayer(), intermediate.getTeams()))
                    .nextAction(stichFinished(intermediate) ? END_STICH : PLAY_CARD);
        }
    }

    public static class EndStich extends Action.BaseAction<Void> {

        public EndStich() {
            super(END_STICH, null, null);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            var playingMode = state.getPlayingMode();
            var tableStack = tableStackCards(state);
            var winningCard = winningCard(playingMode, tableStack);
            var winningCardState = cardState(state, winningCard);
            var winningTeam = teamWith(state, winningCardState.getPlayer());
            var points = tableStackPoints(playingMode, tableStack) + (roundFinished(state) ? 5 : 0);


            var newCards = map(state.getCards(), c -> c.queryIsOnTable() ? c.toBuilder().team(winningTeam.getName()).build() : c);
            var newTeams = map(state.getTeams(), t -> t.equals(winningTeam) ? t.toBuilder().points(t.getPoints() + points).build() : t);

            var intermediateState = state.toBuilder()
                    .cards(newCards)
                    .teams(newTeams)
                    .build();

            return intermediateState.toBuilder()
                    .nextPlayer(roundFinished(intermediateState) ? null : winningCardState.getPlayer())
                    .nextAction(roundFinished(intermediateState) ? END_ROUND : START_STICH);
        }
    }

    public static class EndRound extends Action.BaseAction<Void> {
        public EndRound() {
            super(END_ROUND, null, null);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder().nextAction(gameEnded(state) ? END_GAME : START_ROUND);
        }
    }

    public static class EndGame extends Action.BaseAction<Void> {
        public EndGame() {
            super(END_GAME, null, null);
        }

        @Override
        public State.StateBuilder reduce(State state) {
            return state.toBuilder().nextAction(EXIT);
        }
    }
}
