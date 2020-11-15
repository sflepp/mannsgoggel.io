import { State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { Col, Divider, Row } from 'antd';
import { CodeExecutionResult } from '../../services/CodeExecutionWebWorker';
import { JSONObject } from "../ui/JSONObject";

const ReactJson = require('react-json-view');

interface Props {
    code: string;
    actionResult: CodeExecutionResult;
}

const mapStateToProps = (state: State): Props => {
    return {
        code: state.editor.playerCode,
        actionResult: state.actionResult,
    };
}

const MoveDebugger = (state: Props) => {

    console.log(state.actionResult);

    if (state.actionResult === undefined) {
        return <div>Game hast not started yet.</div>
    }

    const functionName = state.actionResult.fn.match(/([a-zA-Z_{1}][a-zA-Z0-9_]+)(?=\()/g)[0];
    const functionParameters = JSON.parse(`[${state.actionResult.fn.match(/\b[^()]+\((.*)\)$/)[1]}]`);

    let functionRegex = /function\s+(?<name>\w+)\s*\((?<arguments>(?:[^()]+)*)?\s*\)/g,
        match,
        matches = [];

    // eslint-disable-next-line
    while (match = functionRegex.exec(state.code)) {
        matches.push(match.groups);
    }

    const functionDefinition = matches.find((match) => match.name === functionName);
    const argumentNames = functionDefinition.arguments.split(',').map(s => s.trim());


    let result;

    if (state.actionResult.result !== undefined) {
        if (typeof JSON.parse(state.actionResult.result) === 'object') {
            result = <JSONObject name={'result'} object={JSON.parse(state.actionResult.result)}/>
        } else {
            result = <span style={{ fontFamily: 'monospace' }}>{state.actionResult.result}</span>;
        }
    }

    return <div>
        <Row>
            <Col span={18}>
                <h3>Next move</h3>
                <span style={{ fontFamily: 'monospace' }}>{functionName}({argumentNames.join(", ")});</span>
            </Col>
            <Col span={6} style={{textAlign: 'right'}}>
                <h3>Execution time</h3>
                <span style={{ fontFamily: 'monospace' }}>{state.actionResult.executionTime} ms</span>
            </Col>
        </Row>
        <Divider/>
        <Row>
            <Col span={24}>
                <h3>Function call</h3>
                <pre>{functionName}</pre>
                {!!state.actionResult.fn && functionParameters.map((parameter: any, i: number) => (
                    <div key={i} style={{ paddingLeft: '20px' }}>
                        <ReactJson
                            theme={'grayscale:inverted'}
                            iconStyle={'triangle'}
                            indentWidth={2}
                            style={{ display: 'inline-block' }}
                            name={argumentNames[i]}
                            collapsed={true}
                            enableClipboard={false}
                            displayObjectSize={false}
                            displayDataTypes={false}
                            src={parameter}/>
                    </div>)
                )}
                <pre>);</pre>
            </Col>
        </Row>
        <Divider/>
        <Row>
            {state.actionResult.result && <Col span={12}>
                <h3>Result</h3>
                {result}
            </Col>}
            {state.actionResult.error && <Col span={24}>
                <h3>Error</h3>
                {state.actionResult.error}
            </Col>}
        </Row>
    </div>
}

export default connect(mapStateToProps)(MoveDebugger);