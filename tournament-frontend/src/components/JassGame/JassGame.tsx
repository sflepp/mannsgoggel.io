import React from 'react';
import { connect } from 'react-redux';
import { GameState, RemoteActionRequest, State } from '../../reducers';
// @ts-ignore
import SockJsClient from 'react-stomp';
import { Action, setRequestNextAction, setResultCodeExecution, updateGameState, updateSpeed } from '../../actions';
import store from '../../store';
import GameStateView from './GameStateView';
import { CodeExecutionDescription, codeExecutionWorker } from '../../services/CodeExecutionWebWorker';
import { call, put, select, takeEvery } from 'redux-saga/effects'
import { Slider } from 'antd';

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

export function* calculateSaga() {
    yield takeEvery('SET_ACTION_REQUEST', function* (action: Action) {
        try {
            const code = yield select((state: State) => state.editor.playerCode);
            const result = yield call(codeExecutionWorker, code, [evaluateFunction(action.payload)]);
            yield put(setResultCodeExecution(result[0]))
        } catch (e) {
            console.error('saga error', e);
        }
    });
}

const speedReduction = (speed: number): Promise<void> => {
    return new Promise<void>(resolve => {
        const millis = Math.pow(100 - speed, 2);
        if (millis === 0) {
            resolve();
        }
        setTimeout(resolve, millis);
    })
}

export function* sendActionSaga() {
    yield takeEvery('SET_ACTION_RESULT', function* (action: Action) {
        const speed = yield select((state: State) => state.speed);
        yield call(speedReduction, speed)
        webSocket.sendMessage('/app/jass/action', JSON.stringify({
            actionType: action.payload.description,
            payload: JSON.parse(action.payload.result)
        }));
    })
}

const changeSpeed = (speed: number) => {
    store.dispatch(updateSpeed(speed));
}

const ref = (ref: any) => {
    webSocket = ref
}

const createNewGame = () => {
    webSocket.sendMessage('/app/jass/new-game', JSON.stringify({ name: '' }));
}

const onGameStateUpdate = (message: GameState) => {
    store.dispatch(updateGameState(message));
}

const onRequestNextAction = (message: RemoteActionRequest) => {
    store.dispatch(setRequestNextAction(message));
}

export const JassGame = (state: State) => {
    const gameStateView = state.gameState ? (
        <div>
            <h1>Game State</h1>
            <div>
                Next action: {state.gameState.nextAction}
            </div>
            <GameStateView/>
        </div>
    ) : <div>Game has not started.</div>;

    return <div>
        <SockJsClient url='http://localhost:8080/ws' topics={['/game/request-action']} onMessage={onRequestNextAction} ref={ref}/>
        <SockJsClient url='http://localhost:8080/ws' topics={['/game/state']} onMessage={onGameStateUpdate} ref={ref}/>

        <h1>Speed</h1>
        <Slider value={state.speed} tooltipVisible onChange={changeSpeed} />
        {gameStateView}

        <button onClick={createNewGame}>Run</button>
    </div>

}

export default connect(mapStateToProps)(JassGame);