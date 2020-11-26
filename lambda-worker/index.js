console.log('Loading function');

// Deployed by git
exports.handler = async (event, context) => {
    console.log('Received event:', JSON.stringify(event, null, 2));
    console.log('Received context: ', JSON.stringify(context, null, 2));

    eval(event.strategyCode);

    switch (event.parameters.action) {
        case 'DECIDE_SHIFT':
            return {
                actionType: event.parameters.action,
                payload: decideShift(
                    event.parameters.handCards,
                    event.parameters.gameState
                ),
            }
        case 'SET_PLAYING_MODE':
            return {
                actionType: event.parameters.action,
                payload: choosePlayingMode(
                    event.parameters.handCards,
                    event.parameters.gameState
                ),
            }
        case 'START_STICH':
            return {
                actionType: event.parameters.action,
                payload: startStich(
                    event.parameters.handCards,
                    event.parameters.gameState
                ),
            }
        case 'PLAY_CARD':
            return {
                actionType: event.parameters.action,
                payload: playCard(
                    event.parameters.handCards,
                    event.parameters.playableCards,
                    event.parameters.tableStack,
                    event.parameters.gameState
                ),
            }
        default:
            throw Error(`Action ${event.parameters.action} is unknown.`);
    }
};
