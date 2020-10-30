import { GameState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { OtherPlayerCards, PlayerCards, TableStack, TeamStack } from './CardView';


const mapStateToProps = (state: State) => {
    return state.gameState;
}

export const GameStateView = (state: GameState) => {

    const otherPlayers = state.teams.flatMap(t => t.players).filter(p => p !== state.playerName);

    return <div className={'jass-table'} style={{ backgroundImage: 'url("cards/jass_table.png")' }}>
        <div className={'col'} style={{color: 'white'}}>
            <TeamStack gameState={state} team={state.teams[0].name}/>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(180deg)' }}><OtherPlayerCards gameState={state} player={otherPlayers[1]}/>
            </div>
        </div>
        <div className={'col'}></div>

        <div className={'col'}>
            <div style={{ transform: 'rotate(90deg)' }}>
                <OtherPlayerCards gameState={state} player={otherPlayers[2]}/>
            </div>
        </div>
        <div className={'col'}>
            <div>
                <TableStack gameState={state}/>
            </div>
        </div>
        <div className={'col'}>
            <div style={{ transform: 'rotate(-90deg)' }}>
                <OtherPlayerCards gameState={state} player={otherPlayers[0]}/>
            </div>
        </div>

        <div className={'col'}></div>
        <div className={'col'}>
            <div>
                <PlayerCards gameState={state}/>
            </div>
        </div>
        <div className={'col'}>
            <TeamStack gameState={state} team={state.teams[1].name}/>
        </div>
    </div>;
}

export default connect(mapStateToProps)(GameStateView);