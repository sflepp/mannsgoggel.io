import { applyMiddleware, compose, createStore } from 'redux';
import rootReducer from '../reducers/index';
import createSagaMiddleware from 'redux-saga'
import { calculateSaga, sendActionSaga } from '../components/JassGame/JassGame';
import { runTestsSaga } from '../components/CodeEditor/CodeTestRunner';

declare global {
    interface Window {
        __REDUX_DEVTOOLS_EXTENSION_COMPOSE__?: typeof compose;
    }
}

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const sagaMiddleware = createSagaMiddleware();

const store = createStore(
    rootReducer,
    composeEnhancers(applyMiddleware(sagaMiddleware))
);

sagaMiddleware.run(calculateSaga);
sagaMiddleware.run(sendActionSaga);
sagaMiddleware.run(runTestsSaga);


export default store;