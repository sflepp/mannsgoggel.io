import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { OtherPlayerCards, PlayerCards, TableStack, TeamStack } from './CardView';


const mapStateToProps = (state: State) => {
    return state.gameState;
}

export const GameStateView = (state: GameState) => {
    return <div className={'jass-table'} style={{ backgroundImage: 'url("fabric.jpg")' }}>
        <div className={'col'}>
            <div/>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(180deg)' }}>
                <OtherPlayerCards cards={state.cards} player={state.teams[0].players[1]} nextPlayer={state.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <TeamStack cards={state.cards} team={state.teams[1]}/>
            </div>
        </div>

        <div className={'col'}>
            <div style={{ transform: 'rotate(90deg)' }}>
                <OtherPlayerCards cards={state.cards} player={state.teams[1].players[1]} nextPlayer={state.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}>
            <div><TableStack cards={state.cards} nextPlayer={state.nextPlayer} teams={state.teams}/></div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <OtherPlayerCards cards={state.cards} player={state.teams[1].players[0]} nextPlayer={state.nextPlayer}/>
            </div>
        </div>
        <div className={'col'}/>
        <div className={'col'}>
            <div>
                <PlayerCards cards={state.cards} nextPlayer={state.nextPlayer} playerName={state.playerName}/>
            </div>
        </div>
        <div className={'col'}>
            <TeamStack cards={state.cards} team={state.teams[0]}/>
        </div>
    </div>;
}

export default connect(mapStateToProps)(GameStateView);