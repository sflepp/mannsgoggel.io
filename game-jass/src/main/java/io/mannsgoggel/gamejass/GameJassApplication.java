package io.mannsgoggel.gamejass;

import io.mannsgoggel.gamejass.domain.Store;
import io.mannsgoggel.gamejass.domain.actors.LocalGameMasterActor;
import io.mannsgoggel.gamejass.domain.actors.LocalPlayerActor;
import io.mannsgoggel.gamejass.domain.game.JassActions;
import io.mannsgoggel.gamejass.strategy.RandomJassStrategy;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameJassApplication {

	public static void main(String[] args) {
		//SpringApplication.run(GameJassApplication.class, args);

		Store store = new Store();

		LocalGameMasterActor gameMaster = new LocalGameMasterActor(store);
		LocalPlayerActor player1 = new LocalPlayerActor("player-1", new RandomJassStrategy(), store);
		LocalPlayerActor player2 = new LocalPlayerActor("player-2", new RandomJassStrategy(), store);
		LocalPlayerActor player3 = new LocalPlayerActor("player-3", new RandomJassStrategy(), store);
		LocalPlayerActor player4 = new LocalPlayerActor("player-4", new RandomJassStrategy(), store);


		gameMaster.connect();
		player1.connect();
		player2.connect();
		player3.connect();
		player4.connect();


		store.dispatchAction(new JassActions.StartGame());

		while (!store.getCurrentState().getGameEnded()) {
			gameMaster.executeNextActionIfPresent();
			player1.executeNextActionIfPresent();
			player2.executeNextActionIfPresent();
			player3.executeNextActionIfPresent();
			player4.executeNextActionIfPresent();
		}

		System.out.println("adsf");
	}
}
