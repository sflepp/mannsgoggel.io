package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.InvalidAction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.*;
import static io.mannsgoggel.gamejass.domain.game.JassRules.*;
import static java.util.stream.Collectors.toMap;

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
            super(START_GAME, "game-master", null);
        }

        @Override
        public void apply(GameState state) {
            state.setTeams(List.of(
                    new Team(List.of(new Player("player-1"), new Player("player-2"))),
                    new Team(List.of(new Player("player-3"), new Player("player-4")))
            ));
            state.setNextPlayer("game-master");
            state.setNextAction(START_ROUND);
        }
    }

    public static class StartRound extends Action.BaseAction<Void> {
        public StartRound(String player) {
            super(START_ROUND, player, null);
        }

        @Override
        public void apply(GameState state) {
            state.setNextPlayer("game-master");
            state.setNextAction(HAND_OUT_CARDS);
        }
    }

    public static class HandOutCards extends Action.BaseAction<Map<String, List<Card>>> {
        public HandOutCards(String player, Map<String, List<Card>> payload) {
            super(HAND_OUT_CARDS, player, payload);
        }

        @Override
        public void apply(GameState state) {
            getPayload().forEach((key, value) -> state.queryPlayerByName(key).setHandCards(value));
            state.setNextPlayer("game-master");
            state.setNextAction(SET_STARTING_PLAYER);
        }

        @Override
        public Action<Map<String, List<Card>>> toPlayerView(String player) {
            return new HandOutCards(player,
                    getPayload().entrySet().stream()
                            .map(entry ->
                                    entry.getKey().equals(player) ? entry : Map.entry(
                                            entry.getKey(),
                                            entry.getValue().stream()
                                                    .map(Card::hide)
                                                    .collect(Collectors.toList())
                                    )
                            )
                            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }
    }

    public static class SetStartingPlayer extends Action.BaseAction<String> {
        public SetStartingPlayer(String player, String payload) {
            super(SET_STARTING_PLAYER, player, payload);
        }

        @Override
        public void apply(GameState state) {
            state.setNextPlayer(getPayload());
            state.setNextAction(DECIDE_SHIFT);
        }
    }

    public static class DecideShift extends Action.BaseAction<Boolean> {
        public DecideShift(String player, Boolean payload) {
            super(DECIDE_SHIFT, player, payload);
        }

        @Override
        public void apply(GameState state) {
            var shift = getPayload();

            state.setShifted(shift);

            if (shift) {
                var teamMate = state.queryTeamMateFor(state.getNextPlayer());

                state.setNextPlayer(teamMate.getName());
            }

            state.setNextAction(SET_PLAYING_MODE);
        }
    }

    public static class SetPlayingMode extends Action.BaseAction<GameMode.PlayingMode> {
        public SetPlayingMode(String player, GameMode.PlayingMode payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public void apply(GameState state) {
            var playingMode = getPayload();

            state.setPlayingMode(playingMode);

            if (state.getShifted()) {
                var teamMate = state.queryTeamMateFor(state.getNextPlayer());

                state.setNextPlayer(teamMate.getName());
            }

            state.setNextAction(START_STICH);
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
        public void apply(GameState state) {
            var card = getPayload();
            var mode = state.getPlayingMode();
            var tableStack = state.getTableStack();
            var player = state.queryPlayerByName(getPlayer());
            var playerCards = player.getHandCards();

            if (!playableCards(mode, playerCards, tableStack).contains(card)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            state.queryPlayerByName(getPlayer()).removeHandCard(card);
            state.getTableStack().add(card.play(getPlayer()));
            state.setNextPlayer(state.isStichFinished() ? "game-master" : nextPlayer(player, state.getTeams()).getName());
            state.setNextAction(state.isStichFinished() ? END_STICH : PLAY_CARD);
        }
    }

    public static class EndStich extends Action.BaseAction<Void> {

        public EndStich(String player) {
            super(END_STICH, player, null);
        }

        @Override
        public void apply(GameState state) {
            var playingMode = state.getPlayingMode();
            var tableStack = state.getTableStack();
            var winningCard = winningCard(playingMode, tableStack);
            var winningTeam = state.queryTeamWith(winningCard.getPlayer());
            var points = tableStackPoints(playingMode, tableStack) + (state.isRoundFinished() ? 5 : 0);

            winningTeam.getCardStack().addAll(tableStack);
            winningTeam.addPoints(points);
            tableStack.clear();
            state.setNextPlayer(state.isRoundFinished() ? "game-master" : winningCard.getPlayer());
            state.setNextAction(state.isRoundFinished() ? END_ROUND : START_STICH);
        }
    }

    public static class EndRound extends Action.BaseAction<Void> {
        public EndRound(String player) {
            super(END_ROUND, player, null);
        }

        @Override
        public void apply(GameState state) {
            state.setNextAction(
                    state.getTeams().stream().anyMatch(team -> team.getPoints() >= 1500) ? END_GAME : START_ROUND
            );
        }
    }

    public static class EndGame extends Action.BaseAction<Void> {
        public EndGame(String player) {
            super(END_GAME, player, null);
        }

        @Override
        public void apply(GameState state) {
            state.setGameEnded(true);
        }
    }
}
