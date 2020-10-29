import { State } from '../reducers';
import { Affix, Button, Steps } from 'antd';
import React from 'react';
import { connect } from 'react-redux';
import { setNextFlowStep } from '../actions';
import store from '../store';
import JassGame from './JassGame/JassGame';
import CodeEditor from './CodeEditor/CodeEditor';
import CodeTestRunner from './CodeEditor/CodeTestRunner';

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
        <JassGame/>,
        <div>Submit</div>
    ]

    const affixTextFlow = [
        'Play Game',
        'Submit'
    ]

    let disabled = false;

    if (step === 0) {
        disabled = state.codeTest.results.map(e => e.error !== undefined).reduce((a, b) => a || b, false);
    }

    return (
        <div>
            <Steps current={state.flow.currentStep} onChange={setFlowStep}>
                <Step title="Code & Test" description="Code your strategy"/>
                <Step title="Play Game" description="Run against others"/>
                <Step title="Submit" description="Submit your strategy"/>
            </Steps>

            {step <= 2 && <>
                <br/>
                <div style={{ textAlign: 'right' }}>
                    <Affix offsetTop={16}>
                        {(state.flow.currentStep === 0 && <CodeTestRunner/>)}
                        <Button disabled={disabled} type="primary"
                                onClick={() => setFlowStep(state.flow.currentStep + 1)}>
                            {affixTextFlow[step]}
                        </Button>
                    </Affix>
                </div>

                <br/>
            </>}

            {contentFlow[step]}
        </div>
    )
}

export default connect(mapStateToProps)(Flow);