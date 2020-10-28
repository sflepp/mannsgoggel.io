import { compose, createStore } from 'redux';
import rootReducer from '../reducers/index';

declare global {
    interface Window {
        __REDUX_DEVTOOLS_EXTENSION_COMPOSE__?: typeof compose;
    }
}

const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

/* eslint-disable no-underscore-dangle */
const store = createStore(
    rootReducer,
    composeEnhancers()
);
/* eslint-enable */


export default store;