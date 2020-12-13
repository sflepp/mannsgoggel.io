import { GameDebuggerState, State } from '../../reducers';
import React from 'react';
import { connect } from 'react-redux';
import { Affix, Badge, Col, Collapse, Divider, Row, Select, Slider, Switch } from 'antd';
import { setDebuggerSettings } from '../../actions';
import store from '../../store';

// @ts-ignore
import { SelectValue } from 'antd/lib/select';
import MoveDebugger from './MoveDebugger';
import JassBoardView from "../JassGame/JassBoardView";
import JassStateView from "../JassGame/JassStateView";

const { Panel } = Collapse;

const { Option } = Select;

const mapStateToProps = (state: State) => {
    return state;
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

const stateFilter = (state: GameDebuggerState, value: 'ALL' | 'PLAYER_ONLY') => {
    store.dispatch(setDebuggerSettings({
        ...state,
        ...{
            stateFilter: value,
        }
    }))
}

const GameDebugger = (state: State) => {
    const debuggerTitle = (
        <div style={{ width: '100%'}}>
            Debugger
            {state.paused && <span style={{float: 'right'}}><Badge offset={[0,-3]} count={1}/></span>}
        </div>)

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
                    <Collapse defaultActiveKey={['board', 'state']}>
                        <Panel header="Board" key="board">
                            <JassBoardView/>
                        </Panel>
                        <Panel header={stateTitle} key="state">
                            <JassStateView/>
                        </Panel>
                        <Panel header="Debug settings" key="settings">
                            <Row>
                                <Col span={12}>
                                    <h3>Speed {state.debugger.speed}</h3>
                                    <div style={{ paddingRight: '20px' }}>
                                        <Slider value={state.debugger.speed}
                                                onChange={(e: number) => changeSpeed(state.debugger, e)}/>
                                    </div>
                                </Col>
                                <Col span={12}>
                                    <h3>Debug</h3>
                                    <Switch checked={state.debugger.pauseOnTurn}
                                            onChange={(e) => changePauseOnTurn(state.debugger, e)}/> Debug moves
                                </Col>
                            </Row>
                            <Divider/>
                            <Row>
                                <Col span={12}>
                                    <h3>Filter moves</h3>
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
                        <Panel header={debuggerTitle} key="debugger">
                            <MoveDebugger/>
                        </Panel>
                    </Collapse>
                </div>
            </Affix>
        </div>);
}

export default connect(mapStateToProps)(GameDebugger);