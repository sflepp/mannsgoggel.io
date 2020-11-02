import React from 'react';
import { connect } from 'react-redux';
import { RemoteActionRequest, State, WebsocketMessage } from '../../reducers';
// @ts-ignore
import SockJsClient from 'react-stomp';
import {
    Action,
    queueWebsocketMessage,
    setPaused,
    setRequestNextAction,
    setResultCodeExecution,
    updateGameState
} from '../../actions';
import store from '../../store';
import GameStateView from './GameStateView';
import { CodeExecutionDescription, codeExecutionWorker } from '../../services/CodeExecutionWebWorker';
import { actionChannel, call, delay, put, select, take, takeEvery } from 'redux-saga/effects'
import { Col, Row, Statistic } from 'antd';


const mapStateToProps = (state: State) => {
    return state;
};

let webSocket: any;

const evaluateFunction = (action: RemoteActionRequest): CodeExecutionDescription => {
    switch (action.action) {
        case 'DECIDE_SHIFT':
            return {
                description: 'DECIDE_SHIFT',
                fn: `decideShift(${JSON.stringify(action.handCards)},${JSON.stringify(action.gameState)})`
            };
        case 'SET_PLAYING_MODE':
            return {
                description: 'SET_PLAYING_MODE',
                fn: `choosePlayingMode(${JSON.stringify(action.handCards)},${JSON.stringify(action.gameState)})`
            };
        case 'START_STICH':
            return {
                description: 'START_STICH',
                fn: `startStich(${JSON.stringify(action.handCards)},${JSON.stringify(action.gameState)})`
            };
        case 'PLAY_CARD':
            return {
                description: 'PLAY_CARD',
                fn: `playCard(${JSON.stringify(action.handCards)},${JSON.stringify(action.playableCards)},${JSON.stringify(action.tableStack)},${JSON.stringify(action.gameState)})`
            }
        default:
            throw new Error(`Unknown action: ${JSON.stringify(action)}`);
    }
}

export function* handleBackpressure() {
    const channel = yield actionChannel('QUEUE_WEBSOCKET_MESSAGE');
    while (true) {
        const action = (yield take(channel)).payload as WebsocketMessage;
        for (let i = 0; i < 100 - (yield select((state: State) => state.debugger.speed)); i++) {
            yield delay(10);
        }

        while ((yield select((state: State) => state.debugger.paused))) {
        }

        if (action.messageType === 'state') {
            yield put(updateGameState(action.payload));
        }

        if (action.messageType === 'action-request') {
            if ((yield select((state: State) => state.debugger.pauseOnTurn))) {
                yield put(setPaused(true));
            }
            yield put(setRequestNextAction(action.payload));
        }
    }
}

export function* newGameSaga() {
    yield takeEvery('RUN_NEW_GAME', (action: Action) => {
        webSocket.sendMessage('/app/jass/new-game', action.payload);
    });
}

export function* calculateSaga() {
    yield takeEvery('SET_ACTION_REQUEST', function* (action: Action) {
        try {
            const code = yield select((state: State) => state.editor.playerCode);
            const result = yield call(codeExecutionWorker, code, [evaluateFunction(action.payload)], true);
            yield put(setResultCodeExecution(result[0]))
        } catch (e) {
            console.error('saga error', e);
        }
    });
}

export function* sendActionSaga() {
    yield takeEvery('SET_ACTION_RESULT', (action: Action) => {
        webSocket.sendMessage('/app/jass/action', JSON.stringify({
            actionType: action.payload.description,
            payload: JSON.parse(action.payload.result)
        }));
    })
}

const ref = (ref: any) => {
    webSocket = ref
}

const onWebsocketMessage = (message: WebsocketMessage) => {
    store.dispatch(queueWebsocketMessage(message))
}

const mapNextActionToHumanReadable = (state: State) => {
    switch (state.gameState.nextAction) {
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
        case 'START_STICH':
            return 'Start stich';
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

const mapPlayingModeToHumanReadable = (state: State) => {
    switch (state.gameState.playingMode) {
        case 'TOP_DOWN':
            return 'Top down';
        case 'BOTTOM_UP':
            return 'Bottom up';
        case 'TRUMP_SPADES':
            return 'Spades';
        case 'TRUMP_CLUBS':
            return 'Clubs';
        case 'TRUMP_HEARTHS':
            return 'Hearths';
        case 'TRUMP_DIAMOND':
            return 'Diamond';
        default:
            return '-';
    }
}

const mapPlayerToHumanReadable = (state: State) => {
    const players = state.gameState.teams.flatMap(t => t.players);

    switch (players.indexOf(state.gameState.nextPlayer)) {
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

export const JassGame = (state: State) => {
    const gameStateView = state.gameState ? (
        <div>
            <Row>
                <Col span={12}>
                    <Statistic title="Points Team 1 (you)" value={state.gameState.teams[0].points}/>
                </Col>
                <Col span={12}>
                    <Statistic title="Points Team 2" value={state.gameState.teams[1].points}/>
                </Col>
            </Row>

            <Row>
                <Col span={12}>
                    <Statistic title="Next action" value={mapNextActionToHumanReadable(state)}/>
                </Col>
                <Col span={12}>
                    <Statistic title="Next player" value={mapPlayerToHumanReadable(state)}/>
                </Col>
            </Row>

            <Row>
                <Col span={12}>
                    <Statistic title="Trump" value={mapPlayingModeToHumanReadable(state)}/>
                </Col>
                <Col span={12}>
                    <Statistic title="Step" value={state.gameState.revision}/>
                </Col>
            </Row>

            {state.gameState.nextAction !== 'EXIT' && state.debugger.renderGameState && <GameStateView/>}
        </div>
    ) : <></>;

    return <div>
        <SockJsClient url='http://localhost:8080/ws' topics={['/user/game']} onMessage={onWebsocketMessage} ref={ref}/>
        {gameStateView}
    </div>
}

export default connect(mapStateToProps)(JassGame);