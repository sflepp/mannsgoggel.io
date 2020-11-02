import React from 'react';
import './App.css';
import { Breadcrumb, Layout, Menu} from 'antd';
import {
    LaptopOutlined,
    NotificationOutlined,
    UserOutlined
} from '@ant-design/icons';
import Flow from './components/Flow';

const { SubMenu } = Menu;
const { Header, Content, Sider } = Layout;

function App() {
    return (
        <div className="App">
            <Layout>
                <Header className="header">
                    <div className="logo"/>
                    <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['2']}>
                        <Menu.Item key="1">nav 1</Menu.Item>
                        <Menu.Item key="2">nav 2</Menu.Item>
                        <Menu.Item key="3">nav 3</Menu.Item>
                    </Menu>
                </Header>
                <Layout>
                    <Layout style={{ padding: '0 24px 24px' }}>
                        <Breadcrumb style={{ margin: '16px 0' }}>
                            <Breadcrumb.Item>Home</Breadcrumb.Item>
                            <Breadcrumb.Item>List</Breadcrumb.Item>
                            <Breadcrumb.Item>App</Breadcrumb.Item>
                        </Breadcrumb>
                        <Content
                            className="site-layout-background"
                            style={{
                                padding: 24,
                                margin: 0,
                                minHeight: 280,
                            }}>
                            <Flow/>
                        </Content>
                    </Layout>
                </Layout>
            </Layout>
        </div>
    );
}

export default App;
