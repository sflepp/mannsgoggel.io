console.log('Loading function');

// Deployed by git
exports.handler = async (event, context) => {
    console.log('Received event:', JSON.stringify(event, null, 2));
    console.log('Received context: ', JSON.stringify(context, null, 2));

    const blobUrl = URL.createObjectURL(new Blob([event.strategyCode], { type: 'text/javascript' }));
    const strategy = new (await import(blobUrl)).default();

    return {
        actionType: event.parameters.action,
        payload: execute(strategy, event.parameters.action, event.parameters),
    }
};

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
