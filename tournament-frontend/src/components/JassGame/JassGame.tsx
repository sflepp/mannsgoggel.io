import React from 'react';
import { connect } from 'react-redux';
import { State } from '../../reducers';
// @ts-ignore
import SockJsClient from 'react-stomp';
import { setRequestNextAction } from '../../actions';
import store from '../../store';
import GameStateView from './GameStateView';

const mapStateToProps = (state: State) => {
    return state;
};

let webSocket: any;

export const JassGame = (state: State) => {

    const onMessage = (message: any) => {

        console.log(message);

        /** Todo: Move to appropriate place */
        /* switch (message.action) {
            case 'DECIDE_SHIFT':
                testWithWorker(state.editor.playerCode, [
                    `decideShift(${JSON.stringify(message.handCards)},${JSON.stringify(message.gameState)})`
                ]).run().then((data) => {
                    webSocket.sendMessage('/app/jass/action', JSON.stringify({
                        actionType: message.action,
                        payload: data[0]
                    }));
                }).catch(e => console.log(e));
                break;
            case 'SET_PLAYING_MODE':
                testWithWorker(state.editor.playerCode, [
                    `choosePlayingMode(${JSON.stringify(message.handCards)},${JSON.stringify(message.gameState)})`
                ]).run().then((data) => {
                    webSocket.sendMessage('/app/jass/action', JSON.stringify({
                        actionType: message.action,
                        payload: data[0]
                    }));
                }).catch(e => console.log(e));
                break;
            case 'START_STICH':
                testWithWorker(state.editor.playerCode, [
                    `startStich(${JSON.stringify(message.handCards)},${JSON.stringify(message.gameState)})`
                ]).run().then((data) => {
                    webSocket.sendMessage('/app/jass/action', JSON.stringify({
                        actionType: message.action,
                        payload: data[0]
                    }));
                }).catch(e => console.log(e));
                break;
            case 'PLAY_CARD':
                testWithWorker(state.editor.playerCode, [
                    `playCard(${JSON.stringify(message.handCards)},${JSON.stringify(message.playableCards)},${JSON.stringify(message.tableStack)},${JSON.stringify(message.gameState)})`
                ]).run().then((data) => {
                    webSocket.sendMessage('/app/jass/action', JSON.stringify({
                        actionType: message.action,
                        payload: data[0]
                    }));
                }).catch(e => console.log(e));
                break;
        } */

        store.dispatch(setRequestNextAction(message));

    }

    const connect = () => {
        webSocket.sendMessage('/app/jass/new-game', JSON.stringify({ name: '' }));
    }

    const ref = (ref: any) => {
        webSocket = ref
    }

    const gameStateView = state.gameState ? (
        <div>
            <h1>Game State</h1>
            <div>
                Next action: { state.gameState.nextAction }
            </div>
            <GameStateView/>
        </div>
    ) : <div>Game has not started.</div>;

    return <div>
        <SockJsClient url='http://localhost:8080/ws' topics={['/game/request-action']} onMessage={onMessage} ref={ref}/>

        {gameStateView}

        <button onClick={connect}>Run</button>
    </div>

}

export default connect(mapStateToProps)(JassGame);