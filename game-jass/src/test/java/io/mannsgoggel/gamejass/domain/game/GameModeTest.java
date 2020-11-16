package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.game.state.State;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GameModeTest {

    @Test
    public void bottomUp_cardOrder() {
        var testee = new GameModes.BottomUp();

        var higherCard = testee.higherCard(
                new State.Card(State.Card.Color.CLUBS, State.Card.Suit.TEN),
                new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.KING)
        );

        assertThat(higherCard, equalTo(new State.Card(State.Card.Color.CLUBS, State.Card.Suit.TEN)));
    }

    @Test
    public void bottomUp_stichWinningCard() {
        var testee = new GameModes.BottomUp();

        var card1 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.TEN);
        var card2 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.SIX);
        var card3 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.EIGHT);
        var card4 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.ACE);

        var tableStack = List.of(
                card1,
                card2,
                card3,
                card4
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card3));
    }

    @Test
    public void topDown_stichWinningCard() {
        var testee = new GameModes.TopDown();

        var card1 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.TEN);
        var card2 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.SIX);
        var card3 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.EIGHT);
        var card4 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.ACE);

        var tableStack = List.of(
                card1,
                card2,
                card3,
                card4
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card4));
    }

    @Test
    public void trump_stichWinningCard_noTrump() {
        var testee = new GameModes.Trump(State.Card.Color.CLUBS);

        var card1 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.TEN);
        var card2 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.SIX);
        var card3 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.EIGHT);
        var card4 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.ACE);

        var tableStack = List.of(
                card1,
                card2,
                card3,
                card4
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card4));
    }

    @Test
    public void trump_stichWinningCard_jack() {
        var testee = new GameModes.Trump(State.Card.Color.SPADES);

        var card1 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.TEN);
        var card2 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.JACK);
        var card3 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.EIGHT);
        var card4 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.ACE);

        var tableStack = List.of(
                card1,
                card2,
                card3,
                card4
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card2));
    }

    @Test
    public void trump_stichWinningCard_nell() {
        var testee = new GameModes.Trump(State.Card.Color.SPADES);

        var card1 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.TEN);
        var card2 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.ACE);
        var card3 = new State.Card(State.Card.Color.SPADES, State.Card.Suit.NINE);
        var card4 = new State.Card(State.Card.Color.DIAMONDS, State.Card.Suit.ACE);

        var tableStack = List.of(
                card1,
                card2,
                card3,
                card4
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card3));
    }
}