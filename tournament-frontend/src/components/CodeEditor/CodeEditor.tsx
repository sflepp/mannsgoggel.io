import React, { useEffect } from 'react';
import Editor from 'react-simple-code-editor';
// @ts-ignore
import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import { CodeEditorState, State } from '../../reducers';
import { updateCode } from '../../actions';
import { connect } from 'react-redux';
import store from '../../store';
import { Affix, Col, message, Row } from 'antd';
import CodeTestRunner from "./CodeTestRunner";
import GameDebugger from './GameDebugger';
// @ts-ignore

const mapStateToProps = (state: State): CodeEditorState => {
    return state.editor;
}

const onValueChange = (code: string) => {
    store.dispatch(updateCode(code))
}

const onKeyDown = (e: KeyboardEvent) => {
    if (e.keyCode === 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
        e.preventDefault();
        localStorage.setItem('playerCode', store.getState().editor.playerCode)
        message.success('Saved your code in local storage.');
    }
}

const CodeEditor = (state: CodeEditorState) => {

    useEffect(() => {
        document.addEventListener('keydown', onKeyDown, false);

        return () => {
            document.removeEventListener('keydown', onKeyDown)
        }
    });

    console.log('editor render');

    return <div>
        <Row>
            <Col span={24}>
            </Col>
        </Row>
        <Row>
            <Col span={14}>
                <div style={{ position: 'absolute', zIndex: 10, right: 16, top: 16 }}>
                    <Affix offsetTop={16}>
                        {<CodeTestRunner/>}
                    </Affix>
                </div>
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
            </Col>
            <Col span={10}>
                <GameDebugger/>
            </Col>}
        </Row></div>;
}

export default connect(mapStateToProps)(CodeEditor);