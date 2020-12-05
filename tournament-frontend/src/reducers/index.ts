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
    paused: boolean;
}

export interface WebsocketMessage {
    messageType: 'state' | 'action-request';
    payload: GameState | RemoteActionRequest;
}

export interface CodeTestState {
    status: 'FAIL' | 'SUCCESS';
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
    paused: false,
    debugger: !!localStorage.getItem('debugger_settings') ? JSON.parse(localStorage.getItem('debugger_settings')) : {
        speed: 75,
        stateFilter: 'ALL',
        renderGameState: true,
        pauseOnTurn: false,
    },
    codeTest: {
        status: 'SUCCESS',
        results: []
    },
    editor: {
        playerCode: !!localStorage.getItem('playerCodeTS') ? localStorage.getItem('playerCodeTS') :
            `


/**
 * Your Jass strategy
 */
class MyJassStrategy implements Strategy {
    /**
     * «Gschobe»
     * Is called in the beginning of a round if you are the first player.
     * Tells if the player wants to shift the game. 
     */
    shift(hand: Card[], state: GameState): boolean {

        // todo: Implement your strategy

        return false;
    }

    /**
     * Choose a playing mode.
     */
    playingMode(hand: Card[], state: GameState): PlayingMode {
        
        // todo: Implement your strategy
        
        return 'TRUMP_HEARTS'; 
    }
   
    /**
     * This function is called whenever your player needs to play a card. 
     * An element in the playableCards array must be returned.
     */
    play(hand: Card[], playable: Card[], table: Card[], state: GameState): Card {    
        
        // todo: Implement your strategy
        
        return playable[0];
    }
}

// Type definition

type PlayingMode = 'TOP_DOWN' | 'BOTTOM_UP' | 'TRUMP_HEARTS' | 
    'TRUMP_SPADES' | 'TRUMP_DIAMONDS' | 'TRUMP_CLUBS';

interface Card {
    color: 'HEARTS' | 'SPADES' | 'DIAMONDS' | 'CLUBS';
    suit: 'ACE' | 'KING' | 'QUEEN' | 'JACK' | 'TEN' | 'NINE' | 'EIGHT' | 'SEVEN' | 'SIX';
}

interface GameState {
    playerName: string;
    revision: number;
    nextAction: string;
    nextPlayer: string;
    playingMode: PlayingMode;
    shifted: boolean;
    teams: Team[];
    cards: CardState[];
}

interface Team {
    name: string;
    players: string[];
    points: number;
}

interface CardState {
    card: Card;
    player: string;
    playOrder: number;
    team: string;
    isTrump: boolean;
    points: number;
}

/**
 * Interface to implement, do not change.
 */
interface Strategy {
    shift(hand: Card[], state: GameState): boolean;
    playingMode(hand: Card[], state: GameState): PlayingMode;
    play(hand: Card[], playable: Card[], table: Card[], state: GameState): Card;
}

export default MyJassStrategy

`
    }
};

function rootReducer(state: State = initialState, action: Action): State {
    switch (action.type) {
        case 'SET_PAUSED':
            return {
                ...state,
                ...{
                    paused: action.payload,
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
                        status: 'SUCCESS',
                        results: []
                    }
                }
            };
        case 'CODE_TEST_RESULT':
            return {
                ...state,
                ...{
                    codeTest: {
                        status: action.payload.map((r: any) => r.error === undefined)
                            .reduce((a: boolean, b: boolean) => a && b, true) ? 'SUCCESS' : 'FAIL',
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