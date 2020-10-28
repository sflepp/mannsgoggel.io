import React from 'react';
import Editor from 'react-simple-code-editor';
// @ts-ignore
import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import { CodeEditorState, State } from '../../reducers';
import { updateCode } from '../../actions';
import { connect } from 'react-redux';
import store from '../../store';
import CodeTestRunner from './CodeTestRunner';
// @ts-ignore

const mapStateToProps = (state: State) => {
    return state.editor;
}

const CodeEditor = (state: CodeEditorState) => {

    const onValueChange = (code: string) => {
        store.dispatch(updateCode(code))
    }

    return <div>
        <Editor
            value={state.playerCode}
            onValueChange={onValueChange}
            highlight={code => highlight(code, languages.js)}
            padding={20}
            style={{
                fontFamily: '"Fira code", "Fira Mono", monospace',
                fontSize: 16,
            }}
            className="container__editor"
        />
    </div>
}

export default connect(mapStateToProps)(CodeEditor);