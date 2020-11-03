import React from 'react';
import { Card, CardState, GameState, Team } from '../../reducers';

export const TeamStack = (props: { cards: CardState[], team: Team }) => {
    const teamCards = props.cards.filter(c => c.team === props.team.name);

    return <div className={'team-stack stack'}>
        {teamCards.map((c, i) =>
            <div key={i} className={'card-wrapper'}
                 style={{
                     top: `calc(0px + ${i * 0.25}px)`,
                     left: `calc(-35px + ${i * 0.25}px)`,
                 }}>
                <UnknownCardView/>
            </div>)}
    </div>
}

export const TableStack = (props: { cards: CardState[], teams: Team[], nextPlayer: string }) => {
    const tableStack = props.cards
        .filter(s => {
            return s.playOrder > 0 && !s.team
        })
        .sort((a, b) => a.playOrder - b.playOrder);

    const playerOrder = [
        props.teams[0].players[0],
        props.teams[1].players[0],
        props.teams[0].players[1],
        props.teams[1].players[1],
    ];

    const startingPlayerIndex = playerOrder.indexOf(tableStack.length > 0 ? tableStack[0].player : props.nextPlayer);

    const tableStackCards = tableStack
        .map((cs, i) =>
            <div key={JSON.stringify(cs.card)} className={'card-wrapper'}
                 style={{
                     transform: `rotate(calc(180deg + ${startingPlayerIndex * -90}deg - ${i * 90}deg)`,
                 }}>
                <CardView card={cs.card} top={'-35px'}/>
            </div>);

    return <div className={'table-stack stack'}>
        {tableStackCards}
    </div>
}

export const OtherPlayerCards = (props: { cards: CardState[], player: string, nextPlayer: string }) => {
    const alreadyPlayedCardsCount = props.cards.filter(c => c.player === props.player).length;
    const handCardCount = 9 - alreadyPlayedCardsCount;

    return <div className="other-player-hand hand"
                style={{ opacity: props.nextPlayer === props.player ? 1 : 0.5 }}>
        {Array.from(Array(handCardCount)).map((unused, i) =>
            <div key={i} className="card-wrapper" style={{
                top: 'calc(-49px)',
                left: `calc(-35px + ${(i - ((handCardCount - 1) / 2)) * 10}px)`,
                transformOrigin: '50% 50%',
                transform: `rotate(${(i - ((handCardCount - 1) / 2)) * 10}deg)`
            }}>
                <UnknownCardView/>
            </div>)
        }
    </div>
}

export const PlayerCards = (props: { cards: CardState[], playerName: string, nextPlayer: string }) => {
    const handCards = props.cards
        .filter(s => s.player === props.playerName && s.playOrder === undefined)
        .sort((a, b) => a.points - b.points)
        .sort((a, b) => a.card.suit < b.card.suit ? 1 : -1)

    const handCardCount = handCards.length;

    const cards = handCards.map((c, i) =>
        <div key={i} className="card-wrapper" style={{
            top: 'calc(-49px)',
            left: `calc(-35px + ${(i - ((handCardCount - 1) / 2)) * 10}px)`,
            transformOrigin: '50% 50%',
            transform: `rotate(${(i - ((handCardCount - 1) / 2)) * 10}deg)`
        }}>
            <CardView card={c.card}/>
        </div>)

    return <div className={'player-hand hand'}  style={{ opacity: props.nextPlayer === props.playerName ? 1 : 0.5 }}>
        {cards}
    </div>
}

export const UnknownCardView = () => {
    return <div className={'card unknown'} style={{
        backgroundImage: `url(./cards/card_back.svg)`,
    }}>
    </div>
}

export const CardView = (props: { card: Card, top?: string}) => {
    return <div className={'card'} style={{
        top: typeof props.top === 'string' ? props.top: '0px',
        backgroundImage: `url(./cards/${props.card.suit}${props.card.color}.svg)`,
    }}>
    </div>
}
