import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { OtherPlayerCards, PlayerCards, TableStack, TeamStack } from './CardView';


const mapStateToProps = (state: State) => {
    return state.gameState;
}

let i = 0;

export const GameStateView = (state: GameState) => {
    i++;
    console.log(i);
    return <div className={'jass-table'} style={{ backgroundImage: 'url("fabric.jpg")' }}>
        <div className={'col'}>
            <div/>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(180deg)' }}>
                <OtherPlayerCards gameState={state} player={state.teams[0].players[1]}/>
            </div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <TeamStack gameState={state} team={state.teams[1]}/>
            </div>
        </div>

        <div className={'col'}>
            <div style={{ transform: 'rotate(90deg)' }}>
                <OtherPlayerCards gameState={state} player={state.teams[1].players[1]}/>
            </div>
        </div>
        <div className={'col'}>
            <div><TableStack gameState={state}/></div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <OtherPlayerCards gameState={state} player={state.teams[1].players[0]}/>
            </div>
        </div>
        <div className={'col'}/>
        <div className={'col'}>
            <div>
                <PlayerCards gameState={state}/>
            </div>
        </div>
        <div className={'col'}>
            <TeamStack gameState={state} team={state.teams[0]}/>
        </div>
    </div>;
}

export default connect(mapStateToProps)(GameStateView);