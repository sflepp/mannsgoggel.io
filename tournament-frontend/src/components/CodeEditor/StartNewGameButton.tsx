import { State } from '../../reducers';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Button } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { runNewGame, setPaused } from '../../actions';
import store from '../../store';

interface Props {
    filter: string;
    nextAction: string;
    paused: boolean;
    codeTestState: string;
}

const onKeyDown = (e: KeyboardEvent, state: Props) => {
    if (e.keyCode === 119) {
        e.preventDefault();
        if (state.paused) {
            resume();
        } else {
            startNewGame(state.filter);
        }
    }
}

const mapStateToProps = (state: State): Props => {
    return {
        filter: state.debugger.stateFilter,
        nextAction: state.gameState?.nextAction,
        paused: state.paused,
        codeTestState: state.codeTest.status,
    }
}

const startNewGame = (filter: string) => {
    store.dispatch(runNewGame(JSON.stringify({ name: 'asdf', filter: filter })));
}

const resume = () => {
    store.dispatch(setPaused(false));
}

const StartNewGameButton = (state: Props) => {
    const isGameRunning = state.nextAction !== 'EXIT' && state.nextAction !== undefined;

    useEffect(() => {
        const keyDown = (e: KeyboardEvent) => onKeyDown(e, state);

        document.addEventListener('keydown', keyDown, false);

        return () => {
            document.removeEventListener('keydown', keyDown)
        }
    });

    return (
        <div>
            {!state.paused && <Button
                type="primary"
                disabled={state.codeTestState === 'FAIL'}
                icon={<CaretRightOutlined/>}
                loading={isGameRunning}
                onClick={() => startNewGame(state.filter)}>
                {!isGameRunning && <span>Run (F8)</span>}
                {isGameRunning && <span>Running...</span>}
            </Button>}
            {state.paused && <Button
                type="primary"
                icon={<CaretRightOutlined/>}
                onClick={() => resume()}>
                Step (F8)
            </Button>}

        </div>
    );
}

export default connect(mapStateToProps)(StartNewGameButton);