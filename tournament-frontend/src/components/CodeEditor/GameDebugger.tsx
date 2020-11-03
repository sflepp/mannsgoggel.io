import { GameDebuggerState, State } from '../../reducers';
import React from 'react';
import { connect } from 'react-redux';
import { Affix, Button, Checkbox, Col, Collapse, Divider, Row, Select, Slider, Switch } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { runNewGame, setDebuggerSettings, setPaused } from '../../actions';
import store from '../../store';
import JassGame from '../JassGame/JassGame';

// @ts-ignore
import JSONPretty from 'react-json-prettify';
import { SelectValue } from 'antd/lib/select';
import MoveDebugger from './MoveDebugger';
import ReactJson from 'react-json-view';

const { Panel } = Collapse;

const { Option } = Select;

const mapStateToProps = (state: State) => {
    return state;
}

const startNewGame = (state: GameDebuggerState) => {
    store.dispatch(runNewGame(JSON.stringify({ name: 'asdf', filter: state.stateFilter })));
}

const resume = () => {
    store.dispatch(setPaused(false));
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

    return <div>
        <Affix offsetTop={0}>
            <div style={{ backgroundColor: 'white', height: 'calc(100vh)', overflowY: 'scroll' }}>
                <Collapse defaultActiveKey={['state', 'settings', 'debugger', 'console']}>
                    <Panel header="Game state" key="state">
                        <JassGame/>
                    </Panel>
                    <Panel header="Debug settings" key="settings">
                        {!state.paused && <Button
                            type="primary"
                            disabled={state.codeTest.status === 'FAIL'}
                            icon={<CaretRightOutlined/>}
                            loading={isGameRunning}
                            onClick={() => startNewGame(state.debugger)}>
                            Run random game
                        </Button>}
                        {state.paused && <Button
                            type="primary"
                            icon={<CaretRightOutlined/>}
                            onClick={() => resume()}>
                            Step to next
                        </Button>}
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
                                <Switch checked={state.debugger.pauseOnTurn}
                                        onChange={(e) => changePauseOnTurn(state.debugger, e)} /> Debug moves
                                <Divider/>
                                <h4>Rendering</h4>
                                <Switch
                                    checked={state.debugger.renderGameState}
                                    onChange={(e) => renderGameState(state.debugger, e)} /> Show game board
                            </Col>
                        </Row>
                    </Panel>
                    <Panel header="Debugger" key="debugger">
                        <MoveDebugger/>
                    </Panel>
                    <Panel header="Console" key="console">
                        {consoleLogs.map((c) => <div>
                            {JSON.parse(c).map((t: any) => {
                                if (typeof t === 'object') {
                                    return <ReactJson enableClipboard={false} collapsed={true} displayObjectSize={false} displayDataTypes={false} src={t} />
                                }
                                return <div>{t}</div>;
                            })}
                            <Divider/>
                        </div>)}
                    </Panel>
                </Collapse>
            </div>
        </Affix>
    </div>
}

export default connect(mapStateToProps)(GameDebugger);