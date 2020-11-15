import { State } from '../reducers';
import { Steps } from 'antd';
import React from 'react';
import { connect } from 'react-redux';
import { setNextFlowStep } from '../actions';
import store from '../store';
import CodeEditor from './CodeEditor/CodeEditor';

const { Step } = Steps;

const mapStateToProps = (state: State) => {
    return state;
}

const Flow = (state: State) => {

    const setFlowStep = (next: number) => {
        store.dispatch(setNextFlowStep(next));
    }

    const step = state.flow.currentStep;

    const contentFlow = [
        <CodeEditor/>,
        <div>CORS test. This feature is not finished yet.</div>,
        <div>This feature is not finished yet.</div>
    ]

    return (
        <div>
            <Steps current={state.flow.currentStep} onChange={setFlowStep}>
                <Step title="Code & Test" description="Code your strategy"/>
                <Step title="Play Game" description="Run against others"/>
                <Step title="Submit" description="Submit your strategy"/>
            </Steps>

            <br />

            {contentFlow[step]}
        </div>
    )
}

export default connect(mapStateToProps)(Flow);