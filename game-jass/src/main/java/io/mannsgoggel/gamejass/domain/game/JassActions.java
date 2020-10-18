package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.InvalidAction;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.*;

public class JassActions {
    public enum ActionType {
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

    public static class StartRound extends Action.BaseAction<Set<Card>> {
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

    public static class HandOutCards extends Action.BaseAction<List<Player>> {
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

    public static class SetStartingPlayer extends Action.BaseAction<String> {
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

    public static class DecideShift extends Action.BaseAction<Boolean> {
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

    public static class SetPlayingMode extends Action.BaseAction<GameMode.PlayingMode> {
        public SetPlayingMode(String player, GameMode.PlayingMode payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            var playingMode = getPayload();

            state.setGameMode(GameMode.Builder.build(playingMode));

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

    public static class StartStich extends Action.BaseAction<Void> {

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

    public static class PlayCard extends Action.BaseAction<Card> {
        public PlayCard(String player, Card payload) {
            super(PLAY_CARD, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {
            var card = getPayload();
            var handCards = state.getPlayerByName(getPlayer()).getHandCards();
            var tableStack = state.getTableStackWithoutPlayer();
            var player = state.getPlayerByName(getPlayer());
            var playerCards = player.getHandCards();

            if (!state.getGameMode().playableCards(playerCards, tableStack).contains(card)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            handCards.remove(card);
            state.getTableStack().add(Pair.with(getPlayer(), card));

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

    public static class EndStich extends Action.BaseAction<Void> {
        EndStich(String player, Void payload) {
            super(END_STICH, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {

            var winningCard = state.getGameMode().winningCard(state.getTableStackWithoutPlayer());
            var winningPlayer = state.getPlayerNameForPlayedCard(winningCard);
            var winningTeam = state.getTeamWith(winningPlayer);

            winningTeam.obtainCards(state.getTableStack());
            state.setTableStack(new ArrayList<>());

            if (state.isRoundFinished()) {
                state.setCurrentPlayer("game-master");
            } else {
                state.setCurrentPlayer(winningPlayer);
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.isRoundFinished() ? END_ROUND : START_STICH;
        }
    }
}
