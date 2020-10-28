import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';

const mapStateToProps = (state: State) => {
    return state.gameState;
}

export const GameStateView = (state: GameState) => {

    const playedCards = state.cards
        .filter(s => {
            return s.playOrder > 0 && !s.team
        })
        .sort((a, b) => a.playOrder - b.playOrder);

    return <div>
        <h1>Table:</h1>
        {playedCards.map(card => <div key={JSON.stringify(card.card)}>{card.card.color} {card.card.suit}</div>)}
    </div>
}

export default connect(mapStateToProps)(GameStateView);