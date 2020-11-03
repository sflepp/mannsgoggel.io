import { State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { Col, Divider, Row } from 'antd';
import { CodeExecutionResult } from '../../services/CodeExecutionWebWorker';
import ReactJson from 'react-json-view';
import { JSONObject } from "../ui/JSONObject";


const mapStateToProps = (state: State): CodeExecutionResult => {
    return state.actionResult;
}

const MoveDebugger = (state: CodeExecutionResult) => {

    if (state === undefined) {
        return <div>Game hast not started yet.</div>
    }

    const functionName = state.fn.match(/([a-zA-Z]+\()/)[1];
    const functionParameters = JSON.parse(`[${state.fn.match(/(?:\()(.+)+(?:\))/)[1]}]`);

    const fnDefinition = state.fn.match(new RegExp(`/(function\splayCard\(([^\)])*\))/`));
    console.log(fnDefinition);


    let result;

    if (typeof JSON.parse(state.result) === 'object') {
        result = <JSONObject name={'result'} object={JSON.parse(state.result)}/>
    } else {
        result = <span style={{fontFamily: 'monospace'}}>state.result</span>;
    }

    return <div>
        <Row>
            <Col span={12}>
                <h4>Execution time</h4>
                <span style={{fontFamily: 'monospace'}}>{state.executionTime} ms</span>
            </Col>
            <Col span={12}>
                <h4>Next move</h4>
                <span style={{fontFamily: 'monospace'}}>{functionName});</span>
            </Col>
        </Row>
        <Divider/>
        <Row>
            <Col span={24}>
                <h4>Function call</h4>
                <pre>{functionName}</pre>
                {!!state.fn && functionParameters.map((parameter: any, i: number) => (
                    <div style={{ paddingLeft: '20px' }}>
                        <ReactJson
                            theme={'grayscale:inverted'}
                            iconStyle={'triangle'}
                            indentWidth={2}
                            style={{ display: 'inline-block' }}
                            name={"handCards"}
                            collapsed={true}
                            enableClipboard={false}
                            displayObjectSize={false}
                            displayDataTypes={false}
                            src={parameter}/>
                    </div>)
                )}
                <pre>);</pre>
            </Col>
            <Col span={12}>
                <h4>Result</h4>
                {result}
            </Col>
        </Row>
        <Divider/>
        <Row>
            <Col span={24}>
                <h4>Error</h4>
                {state.error}
            </Col>
        </Row>
    </div>
}

export default connect(mapStateToProps)(MoveDebugger);