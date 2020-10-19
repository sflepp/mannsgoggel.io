package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.InvalidAction;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.*;

public class JassActions {
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
        END_GAME
    }

    public static class StartGame extends Action.BaseAction<Void> {
        public StartGame() {
            super(START_GAME, null, null);
        }

        @Override
        public GameState reduce(GameState state) {
            state.setGameEnded(false);
            state.setNextPlayer("game-master");
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return START_ROUND;
        }
    }

    public static class StartRound extends Action.BaseAction<Void> {
        public StartRound(String player) {
            super(START_ROUND, player, null);
        }

        @Override
        public GameState reduce(GameState state) {
            state.setNextPlayer("game-master");
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return HAND_OUT_CARDS;
        }
    }

    public static class HandOutCards extends Action.BaseAction<Map<String, List<Card>>> {
        public HandOutCards(String player, Map<String, List<Card>> payload) {
            super(HAND_OUT_CARDS, player, payload);
        }

        @Override
        public GameState reduce(GameState state) {

            getPayload().forEach((key, value) -> state.getPlayerByName(key).setHandCards(value));

            state.setNextPlayer("game-master");

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
            state.setNextPlayer(getPayload());
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return DECIDE_SHIFT;
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
                var teamMate = state.getTeamMateFor(state.getNextPlayer());

                state.setNextPlayer(teamMate.getName());
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
                var teamMate = state.getTeamMateFor(state.getNextPlayer());

                state.setNextPlayer(teamMate.getName());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return START_STICH;
        }
    }

    public static class StartStich extends PlayCard {
        public StartStich(String player, Card payload) {
            super(START_STICH, player, payload);
        }
    }

    public static class PlayCard extends Action.BaseAction<Card> {
        public PlayCard(ActionType action, String player, Card payload) {
            super(action, player, payload);
        }
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

            state.getPlayerByName(getPlayer()).removeHandCard(card);
            state.getTableStack().add(Pair.with(getPlayer(), card));

            if (state.isStichFinished()) {
                state.setNextPlayer("game-master");
            } else {
                state.setNextPlayer(JassRules.nextPlayer(player, state.getTeams()).getName());
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.isStichFinished() ? END_STICH : PLAY_CARD;
        }
    }

    public static class EndStich extends Action.BaseAction<Void> {
        public EndStich(String player) {
            super(END_STICH, player, null);
        }

        @Override
        public GameState reduce(GameState state) {

            var winningCard = state.getGameMode().winningCard(state.getTableStackWithoutPlayer());
            var winningPlayer = state.getPlayerNameForPlayedCard(winningCard);
            var winningTeam = state.getTeamWith(winningPlayer);
            var points = state.getTableStackWithoutPlayer().stream()
                    .mapToInt(card -> state.getGameMode().getPoints(card))
                    .sum();

            winningTeam.obtainCards(state.getTableStack());
            winningTeam.addPoints(points);
            state.setTableStack(new ArrayList<>());

            if (state.isRoundFinished()) {

                winningTeam.addPoints(5);

                state.setNextPlayer("game-master");
            } else {
                state.setNextPlayer(winningPlayer);
            }

            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.isRoundFinished() ? END_ROUND : START_STICH;
        }
    }

    public static class EndRound extends Action.BaseAction<Void> {
        public EndRound(String player) {
            super(END_ROUND, player, null);
        }

        @Override
        public GameState reduce(GameState state) {
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return state.getTeams().stream().anyMatch(team -> team.getPoints() >= 1500) ? END_GAME : START_ROUND;
        }
    }

    public static class EndGame extends Action.BaseAction<Void> {
        public EndGame(String player) {
            super(END_GAME, player, null);
        }

        @Override
        public GameState reduce(GameState state) {
            state.setGameEnded(true);
            return state;
        }

        @Override
        public ActionType nextAction(GameState state) {
            return END_GAME;
        }
    }
}
