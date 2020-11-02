import { State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { Col, Row, Statistic } from 'antd';


const mapStateToProps = (state: State) => {
    return state;
}

const MoveDebugger = (state: State) => {

    console.log(state.actionResult);
    return <div>
        <Row>
            <Col span={24}>
                <Statistic title="Execution time" value={`${state.actionResult.executionTime} ms`}/>
            </Col>
        </Row>
        <Row>
            <Col span={12}>
                <Statistic title="Function return value" value={state.actionResult.result}/>
            </Col>
            <Col span={12}>
                <Statistic title="Next move" value={state.actionResult.description}/>
            </Col>
        </Row>
        <Row>
            <Col span={24}>
                <Statistic title="Error" value={state.actionResult.error}/>
            </Col>
        </Row>



    </div>
}

export default connect(mapStateToProps)(MoveDebugger);