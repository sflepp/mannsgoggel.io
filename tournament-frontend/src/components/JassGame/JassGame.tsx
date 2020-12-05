import React from 'react';
import { State, WebsocketMessage } from '../../reducers';
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
import { codeExecutionWorker } from '../../services/CodeExecutionWebWorker';
import { actionChannel, call, delay, put, select, take, takeEvery } from 'redux-saga/effects'
import config from "../../config";

let webSocket: any;

export function* handleBackpressure() {
    const channel = yield actionChannel('QUEUE_WEBSOCKET_MESSAGE');
    const pauseChannel = yield actionChannel('SET_PAUSED')
    while (true) {
        const action = (yield take(channel)).payload as WebsocketMessage;
        for (let i = 0; i < 100 - (yield select((state: State) => state.debugger.speed)); i++) {
            yield delay(10);
        }

        while ((yield select((state: State) => state.paused))) {
            yield take(pauseChannel);
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
        const code = yield select((state: State) => state.editor.playerCode);
        const result = yield call(codeExecutionWorker, {
            description: `Remote request ${action.payload.action}`,
            code: code,
            action: action.payload.action,
            parameters: action.payload
        });
        yield put(setResultCodeExecution(result))
    });
}

export function* sendActionSaga() {
    yield takeEvery('SET_ACTION_RESULT', (action: Action) => {
        if (action.payload.result !== undefined) {
            webSocket.sendMessage('/app/jass/action', JSON.stringify({
                actionType: action.payload.action,
                payload: JSON.parse(action.payload.result)
            }));
        }
    })
}

const ref = (ref: any) => {
    webSocket = ref
}

const onWebsocketMessage = (message: WebsocketMessage) => {
    store.dispatch(queueWebsocketMessage(message))
}

export const JassGame = () => {
    return <SockJsClient url={config.websocketUrl} topics={['/user/game']} onMessage={onWebsocketMessage}
                         ref={ref}/>;
}

export default JassGame;