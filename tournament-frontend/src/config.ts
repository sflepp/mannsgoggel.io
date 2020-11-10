interface Config {
    websocketUrl: string;
}

const dev: Config = {
    websocketUrl: 'http://localhost:8080/ws'
}

const prod: Config = {
    websocketUrl: '/ws'
}

const config = process.env.REACT_APP_STAGE === 'prod' ? prod : dev;

export default config;