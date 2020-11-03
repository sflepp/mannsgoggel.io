import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { OtherPlayerCards, PlayerCards, TableStack, TeamStack } from './CardView';

interface Props {
    gameState: GameState;
}

const mapStateToProps = (state: State) => {
    return {
        gameState: state.gameState
    };
}

export const JassBoardView = (state: Props) => {
    console.log(state);

    if (state.gameState === null) {
        return <div>Game hast not startet yet.</div>
    }

    return <div className={'jass-table'} style={{ backgroundImage: 'url("fabric.jpg")' }}>
        <div className={'col'}>
            <div/>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(180deg)' }}>
                <OtherPlayerCards cards={state.gameState.cards} player={state.gameState.teams[0].players[1]} nextPlayer={state.gameState.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <TeamStack cards={state.gameState.cards} team={state.gameState.teams[1]}/>
            </div>
        </div>

        <div className={'col'}>
            <div style={{ transform: 'rotate(90deg)' }}>
                <OtherPlayerCards cards={state.gameState.cards} player={state.gameState.teams[1].players[1]} nextPlayer={state.gameState.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}>
            <div><TableStack cards={state.gameState.cards} nextPlayer={state.gameState.nextPlayer} teams={state.gameState.teams}/></div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <OtherPlayerCards cards={state.gameState.cards} player={state.gameState.teams[1].players[0]} nextPlayer={state.gameState.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}/>
        <div className={'col'}>
            <div>
                <PlayerCards cards={state.gameState.cards} nextPlayer={state.gameState.nextPlayer} playerName={state.gameState.playerName}/>
            </div>
        </div>
        <div className={'col'}>
            <TeamStack cards={state.gameState.cards} team={state.gameState.teams[0]}/>
        </div>
    </div>;
}

export default connect(mapStateToProps)(JassBoardView);