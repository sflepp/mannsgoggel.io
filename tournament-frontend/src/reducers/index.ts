import { Action } from '../actions';
import { CodeExecutionResult } from '../services/CodeExecutionWebWorker';

export interface State {
    nextAction: string;
    gameState: GameState;
    actionRequest?: RemoteActionRequest;
    actionResult?: CodeExecutionResult;
    editor: CodeEditorState;
    debugger: GameDebuggerState;
    codeTest: CodeTestState;
    flow: Flow;
}

export interface WebsocketMessage {
    messageType: 'state' | 'action-request';
    payload: GameState | RemoteActionRequest;
}

export interface CodeTestState {
    status: 'RUNNING' | 'DONE';
    results: CodeExecutionResult[];
}

export interface CodeEditorState {
    playerCode: string;
}

export interface GameDebuggerState {
    speed: number;
    stateFilter: 'ALL' | 'PLAYER_ONLY';
    pauseOnTurn: boolean;
    renderGameState: boolean;
    paused: boolean;
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
    playerName: string;
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
    nextAction: '',
    gameState: null,
    flow: {
        currentStep: 0,
    },
    debugger: !!localStorage.getItem('debugger_settings') ? JSON.parse(localStorage.getItem('debugger_settings')) : {
        speed: 100,
        stateFilter: 'ALL',
        renderGameState: true,
        pauseOnTurn: true,
        paused: false,
    },
    codeTest: {
        status: 'DONE',
        results: []
    },
    editor: {
        playerCode: !!localStorage.getItem('playerCode') ? localStorage.getItem('playerCode') :
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

function rootReducer(state: State = initialState, action: Action): State {
    switch (action.type) {
        case 'SET_PAUSED':
            return {
                ...state,
                ...{
                    debugger: {
                        ...state.debugger,
                        ...{ paused: action.payload }
                    }
                }
            };
        case 'RUN_NEW_GAME':
            break;
        case 'QUEUE_WEBSOCKET_MESSAGE':
            break;
        case 'SET_DEBUGGER_SETTINGS':
            return {
                ...state,
                ...{
                    debugger: action.payload,
                }
            };
        case 'CODE_TEST_REQUEST':
            return {
                ...state,
                ...{
                    codeTest: {
                        status: 'RUNNING',
                        results: []
                    }
                }
            };
        case 'CODE_TEST_RESULT':
            return {
                ...state,
                ...{
                    codeTest: {
                        status: 'DONE',
                        results: action.payload
                    }
                }
            };
        case 'SET_NEXT_FLOW_STEP':
            return {
                ...state,
                ...{
                    flow: {
                        currentStep: action.payload
                    }
                }
            };
        case 'SET_ACTION_REQUEST':
            return {
                ...state,
                ...{ actionRequest: action.payload },
            };
        case 'SET_ACTION_RESULT':
            return {
                ...state,
                ...{ actionResult: action.payload },
            };
        case 'UPDATE_GAME_STATE':
            return {
                ...state,
                ...{ gameState: action.payload }

            };
        case 'UPDATE_CODE':
            return {
                ...state,
                ...{
                    editor: {
                        playerCode: action.payload
                    }
                }
            };
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
            };
    }
    return state;
}

export default rootReducer;