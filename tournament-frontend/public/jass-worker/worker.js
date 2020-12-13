var logFn = console.log;
//console.clear();

self.addEventListener('message', async (event) => {
    const log = [];

    console.log = (...args) => {
        log.push({level: 'log', payload: args});
        logFn('worker', ...args);
    }

    const t0 = performance.now();
    try {
        const blobUrl = URL.createObjectURL(new Blob([event.data.compiledCode], { type: 'text/javascript' }));
        const strategy = new (await import(blobUrl)).default();
        const result = execute(strategy, event.data.action, event.data.parameters);
        const t1 = performance.now()

        if (result === undefined || result === null) {
            throw new Error('Return value is ' + result);
        }

        self.postMessage({
            consoleOutput: log,
            executionTime: parseInt(t1 - t0),
            result: JSON.stringify(result),
            error: null,
        });

    } catch (e) {
        console.error(e);
        const t1 = performance.now()
        self.postMessage({
            consoleOutput: log,
            executionTime: parseInt(t1 - t0),
            result: null,
            error: e.message,
        })
    }
}, false);

const execute = (strategy, action, parameters) => {
    switch (action) {
        case 'DECIDE_SHIFT':
            return strategy.shift(parameters.handCards, parameters.gameState);
        case 'SET_PLAYING_MODE':
            return strategy.playingMode(parameters.handCards, parameters.gameState);
        case 'PLAY_CARD':
            return strategy.play(parameters.handCards, parameters.playableCards, parameters.tableStack, parameters.gameState);
        default:
            throw Error(`Action ${action} is unknown.`);
    }
}