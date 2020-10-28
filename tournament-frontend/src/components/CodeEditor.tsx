import React from 'react';
import Editor from 'react-simple-code-editor';
// @ts-ignore
import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import { CodeEditorState, State } from '../reducers';
import { setCodeError, updateCode } from '../actions';
import { connect } from 'react-redux';
import store from '../store';
import { message } from 'antd';
import { runWebWorker } from '../services/Webworker';
// @ts-ignore

const mapStateToProps = (state: State) => {
    return state.editor;
}

const CodeEditor = (state: CodeEditorState) => {

    console.log(state);

    const onValueChange = (code: string) => {
        store.dispatch(updateCode(code))
    }

    if (state.playerCodeChanged) {
        runWebWorker(state.playerCode, [
            'decideShift([], [])',
            'choosePlayingMode([], {})',
            'startStich([], {})'
        ]).then((data) => {

        }).catch((error) => {
            if (error.message !== undefined) {
                store.dispatch(setCodeError({
                    line: error.lineno,
                    message: error.message
                }));

                message.error(error.message)
            }
        });
    }

    return <div>
        <Editor
            value={state.playerCode}
            onValueChange={onValueChange}
            highlight={code => highlight(code, languages.js)}
            padding={10}
            style={{
                fontFamily: '"Fira code", "Fira Mono", monospace',
                fontSize: 16,
            }}
            className="container__editor"
        />
    </div>


}

export default connect(mapStateToProps)(CodeEditor);