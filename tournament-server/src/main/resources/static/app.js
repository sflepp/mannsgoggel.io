var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/game/state', function (message) {
            console.log('received', message);
            console.log(JSON.parse(message.body))
        });

        stompClient.subscribe('/game/request-action', function (message) {
            var action = JSON.parse(message.body);
            console.log(action);

            switch (action.action) {
                case 'DECIDE_SHIFT':
                    stompClient.send('/app/jass/action', {}, JSON.stringify({
                        actionType: action.action,
                        payload: false
                    }));
                    break;
                case 'SET_PLAYING_MODE':
                    stompClient.send('/app/jass/action', {}, JSON.stringify({
                        actionType: action.action,
                        payload: 'TRUMP_SPADES'
                    }));
                    break;
                case 'START_STICH':
                case 'PLAY_CARD':
                    stompClient.send('/app/jass/action', {}, JSON.stringify({
                        actionType: action.action,
                        payload: action.playableCards[0]
                    }));
            }
        })

        stompClient.send("/app/jass/new-game", {}, JSON.stringify({'name': $("#name").val()}));
    });
}