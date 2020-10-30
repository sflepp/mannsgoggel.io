import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { CardView, PlayerCards, TableStack } from './CardView';

const mapStateToProps = (state: State) => {
    return state.gameState;
}

export const GameStateView = (state: GameState) => {
    return <div>
        <h1>Table:</h1>
        <TableStack gameState={state}/>
        <h1>Player Cards:</h1>
        <PlayerCards gameState={state}/>
    </div>
}

export default connect(mapStateToProps)(GameStateView);