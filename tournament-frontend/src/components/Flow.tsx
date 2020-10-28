import { State } from '../reducers';
import { Affix, Button, Steps } from 'antd';
import React, { useState } from 'react';
import { connect } from 'react-redux';
import { setNextFlowStep } from '../actions';
import store from '../store';
import CodeEditor from './CodeEditor';
import JassGame from './JassGame/JassGame';

const { Step } = Steps;

const mapStateToProps = (state: State) => {
    return state;
}

const Flow = (state: State) => {

    const setFlowStep = (next: number) => {
        store.dispatch(setNextFlowStep(next));
    }

    const [top, setTop] = useState(10);

    const step = state.flow.currentStep;

    let contentFlow = [
        <CodeEditor/>,
        <JassGame/>,
        <div>Submit</div>
    ]

    let affixTextFlow = [
        'Play',
        'Submit'
    ]

    return (
        <div>
            <Steps current={state.flow.currentStep} onChange={setFlowStep}>
                <Step title="Code & Test" description="Code your strategy"/>
                <Step title="Play" description="Run against others"/>
                <Step title="Submit" description="Submit your strategy"/>
            </Steps>

            {step <= 2 && <>
                <br/>
                <div style={{textAlign: 'right'}}>
                    <Affix offsetTop={16}>
                        <Button type="primary" onClick={() => setFlowStep(state.flow.currentStep + 1)}>
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