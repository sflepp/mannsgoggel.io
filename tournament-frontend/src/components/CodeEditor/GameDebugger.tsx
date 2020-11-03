import { GameDebuggerState, State } from '../../reducers';
import React from 'react';
import { connect } from 'react-redux';
import { Affix, Badge, Button, Col, Collapse, Divider, Row, Select, Slider, Switch } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { runNewGame, setDebuggerSettings, setPaused } from '../../actions';
import store from '../../store';

// @ts-ignore
import { SelectValue } from 'antd/lib/select';
import MoveDebugger from './MoveDebugger';
import { ConsoleView } from "./ConsoleView";
import JassBoardView from "../JassGame/JassBoardView";
import JassStateView from "../JassGame/JassStateView";

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

    const debuggerTitle = (
        <div style={{ width: '100%'}}>
            Debugger
            {state.paused && <span style={{float: 'right'}}><Badge offset={[0,-3]} count={1}/></span>}
        </div>)

    const consoleTitle = (
        <div style={{ width: '100%'}}>
            Console
            {state.paused && <span style={{float: 'right'}}><Badge offset={[0,-3]} count={consoleLogs.length}/></span>}
        </div>
    );

    const stateTitle = (
        <div style={{ width: '100%'}}>
            State
            {!!state.gameState && <span style={{float: 'right'}}><Badge style={{ backgroundColor: '#4a4a4a' }} overflowCount={1000} offset={[0,-3]} count={state.gameState.revision}/></span>}
        </div>
    )

    return (
        <div>
            <Affix offsetTop={0}>
                <div style={{ height: 'calc(100vh)', overflowY: 'scroll' }}>
                    <Collapse defaultActiveKey={['settings']}>
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
                                </Col>
                                <Col span={12}>
                                    <h4>Debug</h4>
                                    <Switch checked={state.debugger.pauseOnTurn}
                                            onChange={(e) => changePauseOnTurn(state.debugger, e)}/> Debug moves
                                </Col>
                            </Row>
                            <Divider/>
                            <Row>
                                <Col span={12}>
                                    <h4>Filter moves</h4>
                                    <Select
                                        style={{width: '100%'}}
                                        labelInValue
                                        defaultValue={{ value: (state.debugger.stateFilter as string) } as SelectValue}
                                        onChange={(e: any) => stateFilter(state.debugger, e.value)}>
                                        <Option value="ALL">All moves</Option>
                                        <Option value="PLAYER_ONLY">Your moves only</Option>
                                    </Select>
                                </Col>
                                <Col span={12}>

                                </Col>
                            </Row>
                        </Panel>
                        <Panel header={stateTitle} key="state">
                            <JassStateView/>
                        </Panel>
                        <Panel header="Board" key="board">
                            <JassBoardView/>
                        </Panel>
                        <Panel header={debuggerTitle} key="debugger">
                            <MoveDebugger/>
                        </Panel>
                        <Panel header={consoleTitle} key="console">
                            <ConsoleView logs={consoleLogs}/>
                        </Panel>
                    </Collapse>
                </div>
            </Affix>
        </div>);
}

export default connect(mapStateToProps)(GameDebugger);