package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.InvalidAction;

import java.util.ArrayList;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.*;
import static io.mannsgoggel.gamejass.domain.game.JassRules.*;

public class JassActions {
    public enum ActionType {
        JOIN_PLAYER,
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

    public static class JoinPlayer extends Action.BaseAction<String> {
        public JoinPlayer(String payload) {
            super(JOIN_PLAYER, "any", payload);
        }

        @Override
        public void apply(GameState state) {
            var amountOfPlayers = state.queryPlayers().size();
            var teamNumber = amountOfPlayers / 2;

            if (amountOfPlayers % 2 == 0) {
                state.getTeams().add(new Team("team" + teamNumber, new ArrayList<>(), 0));
            }

            state.getTeams().get(teamNumber).getPlayers().add(getPayload());

            var full = state.queryPlayers().size() == 4;

            state.setNextAction(full ? START_GAME : JOIN_PLAYER);
            state.setNextPlayer(full ? "game-master" : "any");
        }
    }

    public static class StartGame extends Action.BaseAction<Void> {
        public StartGame(String player) {
            super(START_GAME, player, null);
        }

        @Override
        public void apply(GameState state) {
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

    public static class HandOutCards extends Action.BaseAction<CardHandout> {
        public HandOutCards(String player, CardHandout payload) {
            super(HAND_OUT_CARDS, player, payload);
        }

        @Override
        public void apply(GameState state) {
            state.setCards(getPayload().getCards());
            state.setNextPlayer("game-master");
            state.setNextAction(SET_STARTING_PLAYER);
        }

        @Override
        public Action<CardHandout> toPlayerView(String player) {
            return new HandOutCards(player, getPayload().toPlayerView(player));
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

                state.setNextPlayer(teamMate);
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

                state.setNextPlayer(teamMate);
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
            var tableStack = state.queryTableStackCards();
            var player = state.queryPlayerByName(getPlayer());
            var playerCards = state.queryPlayerCards(getPlayer());

            if (!playableCards(mode, playerCards, tableStack).contains(card)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            state.transformCardState(
                    cardState -> cardState.getCard().equals(card) ?
                            cardState.play(state.queryTableStack().size()) :
                            cardState
            );

            state.setNextPlayer(state.queryStichFinished() ? "game-master" : nextPlayer(player, state.getTeams()));
            state.setNextAction(state.queryStichFinished() ? END_STICH : PLAY_CARD);
        }
    }

    public static class EndStich extends Action.BaseAction<Void> {

        public EndStich(String player) {
            super(END_STICH, player, null);
        }

        @Override
        public void apply(GameState state) {
            var playingMode = state.getPlayingMode();
            var tableStack = state.queryTableStackCards();
            var winningCard = winningCard(playingMode, tableStack);
            var winningCardState = state.queryCardState(winningCard);
            var winningTeam = state.queryTeamWith(winningCardState.getPlayer());
            var points = tableStackPoints(playingMode, tableStack) + (state.queryRoundFinished() ? 5 : 0);

            state.transformCardState(
                    cardState -> cardState.queryIsOnTable() ?
                            cardState.moveToTeam(winningTeam.getName()) :
                            cardState
            );

            state.transformTeam(
                    team -> team.equals(winningTeam) ?
                            team.toBuilder().points(team.getPoints() + points).build() :
                            team
            );

            state.setNextPlayer(state.queryRoundFinished() ? "game-master" : winningCardState.getPlayer());
            state.setNextAction(state.queryRoundFinished() ? END_ROUND : START_STICH);
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
        public void apply(GameState state) { }
    }
}
