import React from 'react';
import { GameState } from '../../reducers';

export const TeamStack = (props: { gameState: GameState, team: string }) => {
    const teamCards = props.gameState.cards.filter(c => c.team === props.team);

    return <div className={'team-stack stack'}>
        {teamCards.map((c, i) =>
            <div className={'card-wrapper'}
                 style={{
                     top: `calc(-70px + ${i * 0.5}px)`,
                     left: `calc(-50px + ${i * 0.5}px)`,
                 }}>
                <UnknownCardView/>
            </div>)}
    </div>
}

export const TableStack = (props: { gameState: GameState }) => {
    const tableStack = props.gameState.cards
        .filter(s => {
            return s.playOrder > 0 && !s.team
        })
        .sort((a, b) => a.playOrder - b.playOrder)
        .map((cs, i) =>
            <div className={'card-wrapper'}
                 style={{
                     top: `calc(-100px + ${i * 30}px)`,
                     left: `calc(-70px + ${i * 30}px)`,
                 }}>
                <CardView key={JSON.stringify(cs.card)} card={cs.card}/>
            </div>);

    return <div className={'table-stack stack'}>
        {tableStack}
    </div>
}

export const OtherPlayerCards = (props: { gameState: GameState, player: string }) => {
    const alreadyPlayedCardsCount = props.gameState.cards.filter(c => c.player === props.player).length;
    const handCardCount = 9 - alreadyPlayedCardsCount;

    return <div className="other-player-hand hand">
        {Array.from(Array(handCardCount)).map((unused, i) =>
            <div key={i} className="card-wrapper" style={{
                top: 'calc(-70px)',
                left: `calc(-50px + ${(i - ((handCardCount - 1) / 2)) * 10}px)`,
                transformOrigin: '50% 50%',
                transform: `rotate(${(i - ((handCardCount - 1) / 2)) * 10}deg)`
            }}>
                <UnknownCardView/>
            </div>)
        }
    </div>
}


export const PlayerCards = (props: { gameState: GameState }) => {
    const handCards = props.gameState.cards
        .filter(s => s.player === props.gameState.playerName && s.playOrder === undefined)
        .sort((a, b) => a.points - b.points)
        .sort((a, b) => a.card.suit < b.card.suit ? 1 : -1)

    const handCardCount = handCards.length;

    const cards = handCards.map((c, i) =>
        <div key={i} className="card-wrapper" style={{
            top: 'calc(-70px)',
            left: `calc(-50px + ${(i - ((handCardCount - 1) / 2)) * 10}px)`,
            transformOrigin: '50% 50%',
            transform: `rotate(${(i - ((handCardCount - 1) / 2)) * 10}deg)`
        }}>
            <CardView card={c.card}/>
        </div>)

    return <div className={'player-hand hand'}>
        {cards}
    </div>
}

export const UnknownCardView = () => {
    return <div className={'card'} style={{
        backgroundImage: `url(./cards/card_back.svg)`,
    }}>
    </div>
}

export const CardView = (props: any) => {
    return <div className={'card'} style={{
        backgroundImage: `url(./cards/${props.card.suit}${props.card.color}.svg)`,
    }}>
    </div>
}
