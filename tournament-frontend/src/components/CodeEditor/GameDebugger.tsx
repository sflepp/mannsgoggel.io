import { GameDebuggerState, State } from '../../reducers';
import React from 'react';
import { connect } from 'react-redux';
import { Affix, Button, Checkbox, Col, Collapse, Divider, Row, Select, Slider } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { runNewGame, setDebuggerSettings } from '../../actions';
import store from '../../store';
import JassGame from '../JassGame/JassGame';

// @ts-ignore
import JSONPretty from 'react-json-prettify';
import { SelectValue } from 'antd/lib/select';
import MoveDebugger from './MoveDebugger';

const { Panel } = Collapse;

const { Option } = Select;

const mapStateToProps = (state: State) => {
    return state;
}

const startNewGame = (state: GameDebuggerState) => {
    store.dispatch(runNewGame(JSON.stringify({ name: 'asdf', filter: state.stateFilter })));
}

const changeSpeed = (state: GameDebuggerState, speed: number) => {
    store.dispatch(setDebuggerSettings({
        ...state,
        ...{
            speed: speed,
        }
    }));
}

const changePauseOnTurn = (state: GameDebuggerState, value: boolean) => {
    store.dispatch(setDebuggerSettings({
        ...state,
        ...{
            pauseOnTurn: value,
        }
    }));
}

const renderGameState = (state: GameDebuggerState, value: boolean) => {
    store.dispatch(setDebuggerSettings({
        ...state,
        ...{
            renderGameState: value
        }
    }))
}

const stateFilter = (state: GameDebuggerState, value: 'ALL' | 'PLAYER_ONLY') => {
    store.dispatch(setDebuggerSettings({
        ...state,
        ...{
            stateFilter: value,
        }
    }))
}

const GameDebugger = (state: State) => {
    const consoleLogs = state.actionResult?.consoleOutput || [];
    const isGameRunning = !!state.gameState && state.gameState.nextAction !== 'EXIT';

    const showDebugger = !!state.gameState && state.debugger.paused;

    return <div>
        <Affix offsetTop={0}>
            <div style={{ backgroundColor: 'white', height: 'calc(100vh)', overflowY: 'scroll' }}>
                <Collapse activeKey={showDebugger ? ['state', 'debugger', 'console'] : ['state', 'settings']}>
                    <Panel header="Game state" key="state">
                        <JassGame/>
                    </Panel>
                    <Panel header="Debug settings" key="settings">
                        <Button
                            type="primary"
                            icon={<CaretRightOutlined/>}
                            loading={isGameRunning}
                            onClick={() => startNewGame(state.debugger)}>
                            Run random game
                        </Button>
                        <Divider/>
                        <Row>
                            <Col span={12}>
                                <h4>Speed {state.debugger.speed}</h4>
                                <div style={{ paddingRight: '20px' }}>
                                    <Slider value={state.debugger.speed}
                                            onChange={(e: number) => changeSpeed(state.debugger, e)}/>
                                </div>
                                <Divider/>
                                <h4>Filter moves</h4>
                                <Select
                                    labelInValue
                                    defaultValue={{ value: (state.debugger.stateFilter as string) } as SelectValue}
                                    onChange={(e: any) => stateFilter(state.debugger, e.value)}>
                                    <Option value="ALL">All moves</Option>
                                    <Option value="PLAYER_ONLY">Your moves only</Option>
                                </Select>
                            </Col>
                            <Col span={12}>
                                <h4>Debug</h4>
                                <Checkbox
                                    checked={state.debugger.pauseOnTurn}
                                    onChange={(e) => changePauseOnTurn(state.debugger, e.target.checked)}>
                                    Debug moves
                                </Checkbox>
                                <Divider/>
                                <h4>Rendering</h4>
                                <Checkbox
                                    checked={state.debugger.renderGameState}
                                    onChange={(e) => renderGameState(state.debugger, e.target.checked)}>
                                    Show game board
                                </Checkbox>
                            </Col>
                        </Row>
                    </Panel>
                    <Panel header="Debugger" key="debugger">
                        <MoveDebugger/>
                    </Panel>
                    <Panel header="Console" key="console">
                        {consoleLogs.map((c) => <div>
                            <JSONPretty key={c}
                                        theme={{
                                            background: 'rgb(255, 255, 255)',
                                            brace: 'rgb(51, 51, 51)',
                                            keyQuotes: 'rgb(51, 51, 51)',
                                            valueQuotes: 'rgb(221, 17, 68)',
                                            colon: 'rgb(51, 51, 51)',
                                            comma: 'rgb(51, 51, 51)',
                                            key: 'rgb(51, 51, 51)',
                                            value: {
                                                string: 'rgb(221, 17, 68)',
                                                "null": 'rgb(0, 128, 128)',
                                                number: 'rgb(0, 128, 128)',
                                                "boolean": 'rgb(0, 128, 128)'
                                            },
                                            bracket: 'rgb(51, 51, 51)'
                                        }}
                                        style={{ width: '100%' }} json={JSON.parse(c)} padding={2}/>
                            <Divider/>
                        </div>)}
                    </Panel>
                </Collapse>
            </div>
        </Affix>
    </div>
}

export default connect(mapStateToProps)(GameDebugger);