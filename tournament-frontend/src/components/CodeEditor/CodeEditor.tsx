import React, { useEffect } from 'react';
import { CodeEditorState, State } from '../../reducers';
import { connect } from 'react-redux';
import store from '../../store';
import { Affix, Col, message, Row } from 'antd/lib';
import CodeTestRunner from "./CodeTestRunner";
import GameDebugger from './GameDebugger';
import JassGame from "../JassGame/JassGame";
import StartNewGameButton from "./StartNewGameButton";
import MonacoEditor from "react-monaco-editor/lib";
import { updateCode } from "../../actions";

const mapStateToProps = (state: State): CodeEditorState => {
    return state.editor;
}

const onKeyDown = (e: KeyboardEvent) => {
    if (e.keyCode === 83 && (navigator.platform.match("Mac") ? e.metaKey : e.ctrlKey)) {
        e.preventDefault();
        localStorage.setItem('playerCodeTS', store.getState().editor.playerCode)
        message.success('Saved your code in local storage.');
    }
}

const onValueChange = (code: string) => {
    store.dispatch(updateCode(code))
}

const CodeEditor = (state: CodeEditorState) => {

    useEffect(() => {
        document.addEventListener('keydown', onKeyDown, false);

        return () => {
            document.removeEventListener('keydown', onKeyDown)
        }
    });

    return <div>
        <JassGame/>
        <Row>
            <Col span={24}>
            </Col>
        </Row>
        <Row>
            <Col span={14}>
                <div style={{
                    position: 'absolute',
                    zIndex: 10,
                    right: 16,
                    top: 16,
                    width: '100%',
                    textAlign: 'center'
                }}>
                    <Affix offsetTop={16}>
                        <div style={{ position: 'absolute', right: 0 }}><StartNewGameButton/></div>
                        <div style={{ display: 'inline-block', paddingTop: '4px' }}><CodeTestRunner/></div>
                    </Affix>
                </div>
                <MonacoEditor
                    options={{automaticLayout: true}}
                    language="typescript"
                    theme="vs-dark"
                    value={state.playerCode}
                    onChange={onValueChange}
                />
            </Col>
            <Col span={10}>
                <GameDebugger/>
            </Col>
        </Row></div>;
}

export default connect(mapStateToProps)(CodeEditor);