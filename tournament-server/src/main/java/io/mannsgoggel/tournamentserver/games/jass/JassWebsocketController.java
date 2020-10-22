package io.mannsgoggel.tournamentserver.games.jass;

/* @Service
public class JassWebsocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private JassGame jassGame;
    private RemotePlayerActor player;

    public JassWebsocketController(SimpMessagingTemplate simpMessagingTemplate, SimpMessageSendingOperations messagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/games/new-game")
    public void newGame(String message, Principal principal) {
        System.out.println(message + " " + principal.toString());

        player = new RemotePlayerActor(principal.getName(), simpMessagingTemplate);

        jassGame = new JassGame(
                new LocalGameMasterActor(),
                List.of(
                        player,
                        new LocalPlayerActor("player-2", new RandomJassStrategy()),
                        new LocalPlayerActor("player-3", new RandomJassStrategy()),
                        new LocalPlayerActor("player-4", new RandomJassStrategy())
                )
        );
        jassGame.start();
    }

    @MessageMapping("/games/action")
    public void onAction(RemoteAction action) {
        try {
            player.next(action);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void scheduled() {
        if (jassGame != null) {
            jassGame.dispatchAllPlayers();
        }
    }

    @MessageMapping("/hello")
    @SendToUser("/topic/greetings") // use @SendToUser instead of @SendTo
    public String greeting(String message, Principal principal) throws Exception {
        System.out.println("worked");
        return "testasfd";
    }
} */