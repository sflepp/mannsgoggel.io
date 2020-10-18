package com.manoggeli.gamejass.domain.action;

import com.manoggeli.gamejass.domain.GameRound;
import com.manoggeli.gamejass.domain.gameObjects.Card;
import com.manoggeli.gamejass.domain.gameObjects.GameState;
import com.manoggeli.gamejass.domain.gameObjects.JassRules;
import com.manoggeli.gamejass.domain.gameObjects.Player;
import com.manoggeli.gamejass.domain.mode.bottomup.BottomUpCardValues;
import com.manoggeli.gamejass.domain.mode.topdown.TopDownCardValues;
import com.manoggeli.gamejass.domain.mode.trump.TrumpCardValues;
import org.javatuples.Pair;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import static com.manoggeli.gamejass.domain.action.Action.ActionType.*;
import static com.manoggeli.gamejass.domain.gameObjects.Card.Color.*;
import static com.manoggeli.gamejass.domain.gameObjects.Card.Color.CLUBS;

public interface Action<T> {
    ActionType getAction();

    String getPlayer();

    T getPayload();

    GameState reduce(GameState state);

    ActionType nextAction(GameState state);

    enum ActionType {
        START_ROUND,
        HAND_OUT_CARDS,
        SET_STARTING_PLAYER,
        DECIDE_SHIFT,
        SET_PLAYING_MODE,
        START_STICH,
        PLAY_CARD,
        END_STICH,
        END_ROUND
    }

    abstract class BaseAction<T> implements Action<T> {
        private final ActionType action;
        private final String player;
        private T payload;

        BaseAction(ActionType action, String player, T payload) {
            this.action = action;
            this.player = player;
            this.payload = payload;
        }

        @Override
        public ActionType getAction() {
            return action;
        }

        @Override
        public String getPlayer() {
            return player;
        }

        @Override
        public T getPayload() {
            return payload;
        }
    }

    class StartRound extends BaseAction<Set<Card>> {
        public StartRound(String player, Set<Card> payload) {
            super(START_ROUND, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            state.setCurrentPlayer("game-master");
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return HAND_OUT_CARDS;
        }
    }

    class HandOutCards extends BaseAction<List<Player>> {
        public HandOutCards(String player, List<Player> payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {

            getPayload().forEach(player ->
                    state.getPlayerByName(player.getName()).setHandCards(player.getHandCards())
            );

            state.setCurrentPlayer("game-master");

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return SET_STARTING_PLAYER;
        }
    }

    class SetStartingPlayer extends BaseAction<String> {
        public SetStartingPlayer(String player, String payload) {
            super(SET_STARTING_PLAYER, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            state.setCurrentPlayer(getPayload());
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return SET_PLAYING_MODE;
        }
    }

    class DecideShift extends BaseAction<Boolean> {
        public DecideShift(String player, Boolean payload) {
            super(DECIDE_SHIFT, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            var shift = getPayload();

            state.setShifted(shift);

            if (shift) {
                var teamMate = state.getTeamMateFor(state.getCurrentPlayer());

                state.setCurrentPlayer(teamMate.getName());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return SET_PLAYING_MODE;
        }
    }

    class SetPlayingMode extends BaseAction<GameRound.PlayingMode> {
        public SetPlayingMode(String player, GameRound.PlayingMode payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            var playingMode = getPayload();

            var cardValues = switch (playingMode) {
                case BOTTOM_UP -> new BottomUpCardValues();
                case TOP_DOWN -> new TopDownCardValues();
                case TRUMP_HEARTHS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS -> new TrumpCardValues();
            };

            var trumpColor = switch (playingMode) {
                case TOP_DOWN, BOTTOM_UP -> null;
                case TRUMP_HEARTHS -> HEARTHS;
                case TRUMP_SPADES -> SPADES;
                case TRUMP_DIAMONDS -> DIAMONDS;
                case TRUMP_CLUBS -> CLUBS;
            };

            state.getPlayers().forEach(player -> player.getHandCards()
                    .forEach(card -> {
                        card.setTrump(card.getColor() == trumpColor);
                        card.setPoints(cardValues.getPoints(card));
                        card.setRank(cardValues.getRankOrder(card));
                    }));

            if (state.isShifted()) {
                var teamMate = state.getTeamMateFor(state.getCurrentPlayer());

                state.setCurrentPlayer(teamMate.getName());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return START_STICH;
        }
    }

    class StartStich extends BaseAction<Void> {

        StartStich(String player, Void payload) {
            super(START_STICH, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return PLAY_CARD;
        }
    }

    class PlayCard extends BaseAction<Card> {
        public PlayCard(String player, Card payload) {
            super(PLAY_CARD, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            var card = getPayload();
            var handCards = state.getPlayerByName(getPlayer()).getHandCards();
            var tableStack = state.getTableStack();
            var player = state.getPlayerByName(getPlayer());

            if (!JassRules.canPlayCard(card, tableStack, handCards)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            handCards.remove(card);
            tableStack.push(Pair.with(getPlayer(), card));

            if (state.isStichFinished()) {
                state.setCurrentPlayer("game-master");
            } else {
                state.setCurrentPlayer(JassRules.nextPlayer(player, state.getTeams()).getName());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.isStichFinished() ? END_STICH : PLAY_CARD;
        }
    }

    class EndStich extends BaseAction<Void> {
        EndStich(String player, Void payload) {
            super(END_STICH, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {

            var winningCard = JassRules.winningCard(state.getTableStack());
            var winningTeam = state.getTeamWith(winningCard.getValue0());

            winningTeam.obtainCards(state.getTableStack());
            state.setTableStack(new Stack<>());

            if (state.isRoundFinished()) {
                state.setCurrentPlayer("game-master");
            } else {
                state.setCurrentPlayer(winningCard.getValue0());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.isRoundFinished() ? END_ROUND : START_STICH;
        }
    }
}




