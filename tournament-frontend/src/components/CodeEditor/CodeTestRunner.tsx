import { CodeTestState, State } from '../../reducers';
import { connect } from 'react-redux';
import React from 'react';
import { Action, codeTestResult } from '../../actions';
import { Alert, Badge, Popover } from 'antd/lib';
import { codeExecutionWorker } from '../../services/CodeExecutionWebWorker';
import { call, put, select, takeLatest } from 'redux-saga/effects';

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

export function* runTestsSaga() {
    yield takeLatest('UPDATE_CODE', function* (action: Action) {
        try {
            const tests = [
                {
                    description: 'decideShift() should return a boolean',
                    fn: `typeof decideShift([], []) === 'boolean'`
                },
                {
                    description: 'choosePlayingMode() should return valid playing mode',
                    fn: `['TOP_DOWN', 'BOTTOM_UP', 'TRUMP_HEARTS', 'TRUMP_SPADES', 'TRUMP_DIAMONDS', 'TRUMP_CLUBS'].includes(choosePlayingMode([], {}))`
                }
            ];

            yield put(codeTestResult([]));

            for (var i = 0; i < tests.length; i++) {
                const currentTestResults = (yield select((state: State) => state.codeTest.results));
                yield put(codeTestResult([
                    ...currentTestResults,
                    (yield call(codeExecutionWorker, action.payload, tests[i], true))
                ]));
            }

        } catch (error) {
            yield put(codeTestResult([{
                description: 'Syntax error',
                fn: action.payload,
                executionTime: 0,
                error: JSON.stringify(error)
            }]));
        }
    });
}

const CodeTestRunner = (state: ClassStateValues) => {
    const badges = state.test.results.map(result => {
        let content;
        let status: 'error' | 'success';

        if (result.error !== undefined) {
            content = <Alert message="Failed" description={result.description} type="error" showIcon/>;
            status = 'error';
        } else if (result.result !== 'true') {
            content = <Alert message="Failed" description={result.description} type="error" showIcon/>
            status = 'error';
        } else {
            content = <Alert message="Success" description={result.description} type="success" showIcon/>
            status = 'success';
        }

        return <Popover placement="bottom" key={result.description} content={content}><Badge
            status={status}/></Popover>
    });

    const totalExecutionTime = state.test.results.map(result => result.executionTime).reduce((a, b) => a + b, 0);

    const red = Math.min(200, (totalExecutionTime / 1000 * 255) + 80);
    const green = Math.min(200, ((1 - (totalExecutionTime / 1000)) * 255) + 80);

    return (
        <>
            <div style={{ paddingRight: "10px", whiteSpace: 'nowrap' }}>
                {badges}
                <span style={{ fontWeight: 'bold', color: `rgb(${red}, ${green}, 26)`}}>{totalExecutionTime} ms</span>
            </div>
        </>
    );
}


export default connect(mapStateToProps)(CodeTestRunner);
