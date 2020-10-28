import { Action } from '../actions';

export interface State {
    articles: {
        id: string;
        title: string;
    }[];
    nextAction: string;
    gameState: GameState;
    requests: RemoteActionRequest[];
    editor: CodeEditorState;
    flow: Flow;
}

export interface CodeEditorState {
    playerCode: string;
    playerCodeChanged: boolean;
    error?: any;
}

export interface Flow {
    currentStep: number;
}

export interface RemoteActionRequest {
    action: string;
    handCards: Card[];
    playableCards: Card[];
    tableStack: Card[];
    gameState: GameState;
}

export interface GameState {
    revision: number;
    nextAction: string;
    nextPlayer: string;
    playingMode: string;
    shifted: boolean;
    teams: Team[];
    cards: CardState[];
}

export interface Team {
    name: string;
    players: string[];
    points: number;
}

export interface CardState {
    card: Card;
    player: string;
    playOrder: number;
    team: string;
    isTrump: boolean;
    points: number;
}

export interface Card {
    color: string;
    suit: string;
}

const initialState: State = {
    articles: [
        { id: 'adf', title: 'asf' }
    ],
    nextAction: '',
    gameState: null,
    requests: [],
    flow: {
        currentStep: 0,
    },
    editor: {
        playerCodeChanged: false,
        playerCode:
            `
/**
 * Is called in the beginning of a round if you are the first player.
 *
 * @param {object[]} handCards  The cards in the players hands
 * @param {object} gameState    The current state of the game
 * @returns {boolean}           If your team mate should choose the playing mode («Gschobe»)
 */
function decideShift(handCards, gameState) {
    return false;
}

/**
 * This function chooses a playing mode. Following playing modes are valid:
 * 
 * TOP_DOWN, BOTTOM_UP, TRUMP_HEARTHS, TRUMP_SPADES, TRUMP_DIAMONDS, TRUMP_CLUBS
 *
 * @param {object[]} handCards  The cards in the players hands
 * @param {object} gameState    The current state of the game
 * @returns {string}            The playing mode for this round
 */
function choosePlayingMode(handCards, gameState) {
    return 'TRUMP_HEARTHS';
}

/**
 * This function is called whenever your player starts a «Stich». All hand cards can be played.
 * 
 * @param {object[]} handCards  The cards in the players hands
 * @param {object} gameState    The current state of the game
 * @returns {object}            The card to play as first player
 */
function startStich(handCards, gameState) {
    return playCard(handCards, handCards, [], gameState);
}

/**
 * This function is called whenever your player needs to play. Only the playableCards can be played.
 * 
 * @param {object[]} handCards      The cards in the players hands
 * @param {object[]} playableCards  The playable cards in the players hand
 * @param {object[]} tableStack     The cards laying on the table in correct order
 * @param {object} gameState        The current state of the game
 * @returns {object}                The card to play
 */
function playCard(handCards, playableCards, tableStack, gameState) {
    return playableCards[0];
}
`
    }
};

function rootReducer(state: State = initialState, action: Action) {
    switch (action.type) {
        case 'SET_NEXT_FLOW_STEP':
            return {
                ...state,
                ...{
                    flow: {
                        currentStep: action.payload
                    }
                }
            };
        case 'SET_REQUEST_NEXT_ACTION':
            return {
                ...state,
                ...{ nextAction: action.payload.nextAction },
                ...{ gameState: action.payload.gameState }
            }
        case 'UPDATE_GAME_STATE':
            return {
                ...state,
                ...{ gameState: action.payload }

            };
        case 'ADD_ARTICLE':
            return {
                ...state,
                ...{
                    articles: state.articles.concat(action.payload)
                }
            };
        case 'UPDATE_CODE':
            return {
                ...state,
                ...{
                    editor: {
                        playerCodeChanged: true,
                        playerCode: action.payload
                    }
                }
            }
        case 'SET_CODE_ERROR':
            return {
                ...state,
                ...{
                    editor: {
                        ...state.editor, ...{
                            playerCodeChanged: false,
                            error: action.payload
                        }
                    }
                }
            }
    }
    return state;
}

export default rootReducer;