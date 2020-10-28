import { CodeTestState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import store from '../../store';
import { Unsubscribe } from 'redux';
import { codeTestResult } from '../../actions';
import { Badge, Popover } from 'antd';
import { testWorker } from '../../services/TestingWebWorker';
import { Alert } from 'antd';

interface ClassStateValues {
    code: string;
    test: CodeTestState
}

function mapStateToProps(state: State): ClassStateValues {
    return {
        code: state.editor.playerCode,
        test: state.codeTest
    };
}

class CodeTestRunner extends React.Component<ClassStateValues> {
    private worker: Worker;
    private unsubscribe: Unsubscribe;
    private currentState: ClassStateValues;

    componentDidMount() {
        this.unsubscribe = store.subscribe(() => {
            let previousState = this.currentState;
            this.currentState = mapStateToProps(store.getState());

            if (this.currentState.code !== previousState?.code) {
                if (this.currentState.test.status === 'RUNNING') {
                    this.worker.terminate();
                }

                const code = this.currentState.code;

                const tests = [
                    {
                        description: 'decideShift() should return a boolean',
                        fn: `typeof decideShift([], []) === 'boolean'`
                    },
                    {
                        description: 'choosePlayingMode() should return valid playing mode',
                        fn: `['TOP_DOWN', 'BOTTOM_UP', 'TRUMP_HEARTHS', 'TRUMP_SPADES', 'TRUMP_DIAMONDS', 'TRUMP_CLUBS'].includes(choosePlayingMode([], {}))`
                    }
                ];

                const worker = testWorker(code, tests);

                this.worker = worker.instance;

                worker.run()
                    .then(result => {
                        store.dispatch(codeTestResult(result))
                    })
                    .catch(error => {
                        store.dispatch(codeTestResult([{
                            description: 'Syntax error',
                            fn: code,
                            error: JSON.stringify(error)
                        }]));
                    })

                this.worker = worker.instance;
            }
        });
    }

    componentWillUnmount() {
        this.unsubscribe();

        if (this.worker !== undefined) {
            this.worker.terminate();
        }
    }

    render() {
        const badges = this.props.test.results.map(result => {
            let content;
            let status: 'error' | 'success';

            if (result.error !== undefined) {
               content = <Alert message="Failed" description={result.description} type="error" showIcon />;
               status = 'error';
            } else if (result.result !== 'true') {
                content = <Alert message="Failed" description={result.description} type="error" showIcon />
                status = 'error';
            } else {
                content = <Alert message="Success" description={result.description} type="success" showIcon />
                status = 'success';
            }

            return <Popover placement="bottom" key={result.description} content={content}><Badge status={status}/></Popover>
        })

        return (<div style={{ display: "inline", paddingRight: "10px" }}>{badges}</div>);
    }
}


export default connect(mapStateToProps)(CodeTestRunner);
