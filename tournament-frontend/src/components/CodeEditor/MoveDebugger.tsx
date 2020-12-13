import { Col, Divider, Row } from 'antd';
import React from 'react';
import { connect } from 'react-redux';
import { State } from '../../reducers';
import { CodeExecutionRequest, CodeExecutionResult } from '../../services/CodeExecutionWebWorker';
import DebugParameter from "../ui/DebugParameter";
import { ConsoleView } from "./ConsoleView";

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

const calculateFunctionName = (action: string) => {
  switch (action) {
    case 'DECIDE_SHIFT':
      return 'shift';
    case 'SET_PLAYING_MODE':
      return 'playingMode';
    case 'PLAY_CARD':
      return 'play';
  }
}

const calculateParameterNames = (action: string) => {
  switch (action) {
    case 'DECIDE_SHIFT':
      return ['hand', 'state'];
    case 'SET_PLAYING_MODE':
      return ['hand', 'state'];
    case 'PLAY_CARD':
      return ['hand', 'playable', 'table', 'state'];
  }
}

const calculateFunctionArguments = (request: CodeExecutionRequest) => {
  switch (request.action) {
    case 'DECIDE_SHIFT':
      return [request.parameters.handCards, request.parameters.state];
    case 'SET_PLAYING_MODE':
      return [request.parameters.handCards, request.parameters.state];
    case 'PLAY_CARD':
      return [request.parameters.handCards, request.parameters.playableCards, request.parameters.tableCards, request.parameters.state];
  }
}


const MoveDebugger = (state: Props) => {
  if (state.actionResult === undefined) {
    return (<div>Game hast not started yet.</div>)
  }

  const functionName = calculateFunctionName(state.actionResult.request.action)
  const parameterNames = calculateParameterNames(state.actionResult.request.action);
  const argumentsPayload = calculateFunctionArguments(state.actionResult.request);

  const consoleLogs = state.actionResult?.consoleOutput || [];

  return <div>
    <Row>
      <Col span={24}>
        <h3>Executed function (<span>{state.actionResult.executionTime} ms</span>)</h3>
        <span
          style={{ fontFamily: 'monospace' }}>
          const <DebugParameter name={'result'} object={state.actionResult.result}
                                isLast={true}/>&nbsp;= Strategy.{functionName}({parameterNames
          .map((parameterName: string, i: number) => {
            return <DebugParameter key={parameterName} name={parameterName} object={JSON.stringify(argumentsPayload[i])}
                                   isLast={i === parameterNames.length - 1}/>
          })});</span>
      </Col>
    </Row>
    {state.actionResult.error && (
      <>
        <Divider/>
        <Row>
          {state.actionResult.error && <Col span={24}>
              <h3>Error</h3>
            {state.actionResult.error}
          </Col>}
        </Row>
      </>)}

    <Divider/>

    <Row>
      <Col>
      <h3>Console</h3>
      <ConsoleView logs={consoleLogs}/>
      </Col>
    </Row>
  </div>
}

export default connect(mapStateToProps)(MoveDebugger);