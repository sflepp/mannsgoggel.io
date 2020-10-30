import React from 'react';
import { GameState } from '../../reducers';

export const OtherPlayerCards = (props: { gameState: GameState }) => {

}


export const PlayerCards = (props: { gameState: GameState }) => {
    const handCards = props.gameState.cards
        .filter(s => s.player === props.gameState.playerName && s.playOrder === undefined)
        .sort((a, b) => a.points - b.points)
        .sort((a, b) => a.card.suit < b.card.suit ? 1 : -1)

    const cards = handCards.map((c, i) =>
        <div key={JSON.stringify(c.card)} className="card-wrapper" style={{
            top: '20px',
            left: `${(i * 20) + 40}px`,
            transformOrigin: '50% 50%',
            transform: `rotate(${(i - ((handCards.length - 1) / 2)) * 10}deg)`
        }}>
            <CardView card={c.card}/>
        </div>)

    return <div className={'player-hand'}>
        {cards}
    </div>
}

export const TableStack = (props: { gameState: GameState }) => {
    const tableStack = props.gameState.cards
        .filter(s => {
            return s.playOrder > 0 && !s.team
        })
        .sort((a, b) => a.playOrder - b.playOrder)
        .map(cs => <CardView key={JSON.stringify(cs.card)} card={cs.card}/>);

    return <div className={'table-stack'}>
        {tableStack}
    </div>
}


export const CardView = (props: any) => {
    return <div className={'card'} style={{
        backgroundImage: `url(./cards/${props.card.suit}${props.card.color}.svg)`,
    }}>
    </div>
}
