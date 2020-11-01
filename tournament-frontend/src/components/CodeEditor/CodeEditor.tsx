import React, { useEffect } from 'react';
import Editor from 'react-simple-code-editor';
// @ts-ignore
import { highlight, languages } from 'prismjs/components/prism-core';
import 'prismjs/components/prism-clike';
import 'prismjs/components/prism-javascript';
import { State } from '../../reducers';
import { runNewGame, updateCode } from '../../actions';
import { connect } from 'react-redux';
import store from '../../store';
import { Affix, Col, List, Row, message, Button } from 'antd';
import CodeTestRunner from "./CodeTestRunner";
import JassGame from "../JassGame/JassGame";
// @ts-ignore

const mapStateToProps = (state: State) => {
    return state;
}

const newGame = () => {
    store.dispatch(runNewGame(JSON.stringify({ name: '' })));
}

const CodeEditor = (state: State) => {

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

    useEffect(() => {
        document.addEventListener('keydown', onKeyDown, false);

        return () => {
            document.removeEventListener('keydown', onKeyDown)
        }
    });

    const consoleLogs = state.codeTest.results
        .flatMap(result => result.consoleOutput)
        .filter(e => typeof e === 'string');


    const hasLogs = consoleLogs.length > 0;

    return <div>
        <Row>
            <Col span={24}>
                <div style={{ float: 'right' }}>
                    <Affix offsetTop={16}>
                        {<CodeTestRunner/>}
                        <Button type="primary" onClick={newGame}>
                            Debug with random game
                        </Button>
                    </Affix>
                </div>
                <div>
                    <JassGame/>
                </div>
            </Col>
        </Row>
        <Row>
            <Col span={hasLogs ? 16 : 24}>
                <Editor
                    value={state.editor.playerCode}
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
            {hasLogs && <Col span={8}>
                <Affix offsetTop={80}>
                    <div style={{ backgroundColor: 'white', height: 'calc(100vh - 80px)', overflowY: 'scroll' }}>
                        <List
                            bordered
                            header={<b>Console</b>}
                            dataSource={consoleLogs}
                            renderItem={item => (<List.Item>{item}</List.Item>)}
                        />
                    </div>
                </Affix>
            </Col>}
        </Row></div>;
}

export default connect(mapStateToProps)(CodeEditor);