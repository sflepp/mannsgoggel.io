import { State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { Col, Row, Statistic } from 'antd';
import { CodeExecutionResult } from '../../services/CodeExecutionWebWorker';
import ReactJson from 'react-json-view';


const mapStateToProps = (state: State): CodeExecutionResult => {
    return state.actionResult;
}

const MoveDebugger = (state: CodeExecutionResult) => {

    if (state === undefined) {
        return <div>someting's wrong</div>
    }

    return <div>
        <Row>
            <Col span={24}>
                <Statistic title="Execution time" value={`${state.executionTime} ms`}/>
            </Col>
        </Row>
        <Row>
            <Col span={12}>
                <h4>Result</h4>
                {!!state.result && <ReactJson collapsed={true} displayObjectSize={false} displayDataTypes={false} src={JSON.parse(state.result)} />};
            </Col>
            <Col span={12}>
                <Statistic title="Next move" value={state.description}/>
            </Col>
        </Row>
        <Row>
            <Col span={24}>
                <Statistic title="Error" value={state.error}/>
            </Col>
        </Row>


    </div>
}

export default connect(mapStateToProps)(MoveDebugger);