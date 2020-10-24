package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import io.mannsgoggel.gamejass.domain.action.InvalidAction;

import static io.mannsgoggel.gamejass.domain.game.JassActions.ActionType.*;
import static io.mannsgoggel.gamejass.domain.game.JassRules.*;

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
        END_GAME,
        EXIT
    }

    public static class StartGame extends Action.BaseAction<Void> {
        public StartGame() {
            super(START_GAME, null, null);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            return state.toBuilder().nextAction(START_ROUND);
        }
    }

    public static class StartRound extends Action.BaseAction<Void> {
        public StartRound() {
            super(START_ROUND, null, null);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            return state.toBuilder().nextAction(HAND_OUT_CARDS);

        }
    }

    public static class HandOutCards extends Action.BaseAction<CardHandout> {
        public HandOutCards(CardHandout payload) {
            super(HAND_OUT_CARDS, null, payload);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            return state.toBuilder()
                    .cards(getPayload().getCards())
                    .nextAction(SET_STARTING_PLAYER);
        }

        @Override
        public Action<CardHandout> toPlayerView(String player) {
            return new HandOutCards(getPayload().toPlayerView(player));
        }
    }

    public static class SetStartingPlayer extends Action.BaseAction<String> {
        public SetStartingPlayer(String payload) {
            super(SET_STARTING_PLAYER, null, payload);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
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
        public GameState.GameStateBuilder build(GameState state) {
            var shift = getPayload();
            var newState = state.toBuilder()
                    .shifted(shift)
                    .nextAction(SET_PLAYING_MODE);

            if (shift) {
                var teamMate = state.queryTeamMateFor(state.getNextPlayer());
                newState.nextPlayer(teamMate);
            }

            return newState;
        }
    }

    public static class SetPlayingMode extends Action.BaseAction<GameMode.PlayingMode> {
        public SetPlayingMode(String player, GameMode.PlayingMode payload) {
            super(SET_PLAYING_MODE, player, payload);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            var playingMode = getPayload();

            var newState = state.toBuilder()
                    .playingMode(playingMode);


            if (state.getShifted()) {
                var teamMate = state.queryTeamMateFor(state.getNextPlayer());
                newState.nextPlayer(teamMate);
            }

            return newState.nextAction(START_STICH);
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
        public GameState.GameStateBuilder build(GameState state) {
            var card = getPayload();
            var mode = state.getPlayingMode();
            var tableStack = state.queryTableStackCards();
            var player = state.queryPlayerByName(getPlayer());
            var playerCards = state.queryHandCards(getPlayer());


            if (!playableCards(mode, playerCards, tableStack).contains(card)) {
                throw new InvalidAction("Playing card " + card.toString() + " is not allowed.");
            }

            var intermediate = state.transformCardState(cardState ->
                    cardState.getCard().equals(card) ? cardState.play(state.queryTableStack().size()) : cardState);

            return intermediate
                    .toBuilder()
                    .nextPlayer(intermediate.queryStichFinished() ? null : nextPlayer(player, intermediate.getTeams()))
                    .nextAction(intermediate.queryStichFinished() ? END_STICH : PLAY_CARD);
        }
    }

    public static class EndStich extends Action.BaseAction<Void> {

        public EndStich() {
            super(END_STICH, null, null);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            var playingMode = state.getPlayingMode();
            var tableStack = state.queryTableStackCards();
            var winningCard = winningCard(playingMode, tableStack);
            var winningCardState = state.queryCardState(winningCard);
            var winningTeam = state.queryTeamWith(winningCardState.getPlayer());
            var points = tableStackPoints(playingMode, tableStack) + (state.queryRoundFinished() ? 5 : 0);

            var intermediate = state
                    .transformCardState(
                            cardState -> cardState.queryIsOnTable() ?
                                    cardState.moveToTeam(winningTeam.getName()) :
                                    cardState
                    )
                    .transformTeam(
                            team -> team.equals(winningTeam) ?
                                    team.toBuilder().points(team.getPoints() + points).build() :
                                    team
                    );

            return intermediate.toBuilder()
                    .nextPlayer(intermediate.queryRoundFinished() ? null : winningCardState.getPlayer())
                    .nextAction(intermediate.queryRoundFinished() ? END_ROUND : START_STICH);
        }
    }

    public static class EndRound extends Action.BaseAction<Void> {
        public EndRound() {
            super(END_ROUND, null, null);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            return state.toBuilder()
                    .nextAction(state.getTeams().stream().anyMatch(team -> team.getPoints() >= 1500) ? END_GAME : START_ROUND);
        }
    }

    public static class EndGame extends Action.BaseAction<Void> {
        public EndGame() {
            super(END_GAME, null, null);
        }

        @Override
        public GameState.GameStateBuilder build(GameState state) {
            return state.toBuilder().nextAction(EXIT);
        }
    }
}
