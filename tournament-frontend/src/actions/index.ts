import { CodeRunResult } from '../reducers';

export type ActionType = 'ADD_ARTICLE'
    | 'UPDATE_GAME_STATE'
    | 'SET_REQUEST_NEXT_ACTION'
    | 'UPDATE_CODE'
    | 'SET_CODE_ERROR'
    | 'SET_NEXT_FLOW_STEP'
    | 'CODE_TEST_REQUEST'
    | 'CODE_TEST_RESULT'

export interface Action {
    type: ActionType;
    payload: any;
}

export function addArticle(payload: any): Action {
    return { type: "ADD_ARTICLE", payload: payload }
}

export function setRequestNextAction(payload: any): Action {
    return { type: 'SET_REQUEST_NEXT_ACTION', payload: payload }
}

export function updateGameState(payload: any): Action {
    return { type: 'UPDATE_GAME_STATE', payload: payload }
}

export function updateCode(payload: string): Action {
    return { type: 'UPDATE_CODE', payload: payload }
}

export function setNextFlowStep(payload: number): Action {
    return { type: 'SET_NEXT_FLOW_STEP', payload: payload }
}

export function setCodeError(payload: any): Action {
    return { type: 'SET_CODE_ERROR', payload: payload }
}

export function codeTestRequest(): Action {
    return { type: 'CODE_TEST_REQUEST', payload: null }
}

export function codeTestResult(payload: CodeRunResult[]): Action {
    return { type: 'CODE_TEST_RESULT', payload: payload }
}