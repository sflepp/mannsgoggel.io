import { GameState, State } from "../../reducers";
import { Col, Row } from "antd";
import { connect } from "react-redux";
import React from "react";

interface Props {
    gameState: GameState,
    renderView: boolean
}

const mapStateToProps = (state: State): Props => {
    return {
        gameState: state.gameState,
        renderView: state.debugger.renderGameState
    };
};

const mapNextActionToHumanReadable = (state: GameState) => {
    switch (state.nextAction) {
        case 'START_ROUND':
            return 'Start round';
        case 'DECIDE_SHIFT':
            return 'Decide shift';
        case 'SET_STARTING_PLAYER':
            return 'Set starting player';
        case 'SET_PLAYING_MODE':
            return 'Choose playing mode';
        case 'PLAY_CARD':
            return 'Play card';
        case 'START_GAME':
            return 'Start game';
        case 'HAND_OUT_CARDS':
            return 'Hand out cards';
        case 'END_STICH':
            return 'End stich';
        case 'END_ROUND':
            return 'End round';
        case 'END_GAME':
            return 'Game ended'
        case 'EXIT':
            return 'Game ended'
        default:
            return '';
    }
}

const mapPlayerToHumanReadable = (state: GameState) => {
    const players = state.teams.flatMap(t => t.players);

    switch (players.indexOf(state.nextPlayer)) {
        case 0:
            return 'Team 1 / You';
        case 1:
            return 'Team 1 / Player-2'
        case 2:
            return 'Team 2 / Player-3';
        case 3:
            return 'Team 2 / Player-4';
        default:
            return '-';
    }
}

const mapPlayingModeToHumanReadable = (state: GameState) => {
    switch (state.playingMode) {
        case 'TOP_DOWN':
            return 'Top down';
        case 'BOTTOM_UP':
            return 'Bottom up';
        case 'TRUMP_SPADES':
            return 'Spades';
        case 'TRUMP_CLUBS':
            return 'Clubs';
        case 'TRUMP_HEARTS':
            return 'Hearts';
        case 'TRUMP_DIAMOND':
            return 'Diamond';
        default:
            return '-';
    }
}

export const JassStateView = (state: Props) => {
    const gameStateView = state.gameState ? (
        <div>
            <Row>
                <Col span={12}>
                    <h3>Points Team 1 (you)</h3>
                    {state.gameState.teams[0].points}
                </Col>
                <Col span={12}>
                    <h3>Points Team 2</h3>
                    {state.gameState.teams[1].points}
                </Col>
            </Row>

            <br />

            <Row>
                <Col span={12}>
                    <h3>Next action</h3>
                    {mapNextActionToHumanReadable(state.gameState)}
                </Col>
                <Col span={12}>
                    <h3>Next player</h3>
                    {mapPlayerToHumanReadable(state.gameState)}
                </Col>
            </Row>

            <br />
            <Row>
                <Col span={12}>
                    <h3>Trump</h3>
                    {mapPlayingModeToHumanReadable(state.gameState)}
                </Col>
                <Col span={12}>
                    <h3>Step</h3>
                    {state.gameState.revision}
                </Col>
            </Row>
        </div>
    ) : <div>Game has not started yet.</div>;

    return <div>
        {gameStateView}
    </div>
}

export default connect(mapStateToProps)(JassStateView);