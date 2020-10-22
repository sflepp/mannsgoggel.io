var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/game/state', function (greeting) {
            console.log('received', greeting);
            console.log(JSON.parse(greeting.body))
        });

        stompClient.subscribe('/game/request-action', function(action) {
            console.log(JSON.parse(action.body));
        })

        stompClient.send("/app/jass/new-game", {}, JSON.stringify({'name': $("#name").val()}));
    });
}