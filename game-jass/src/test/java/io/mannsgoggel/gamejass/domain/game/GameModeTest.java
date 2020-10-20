package io.mannsgoggel.gamejass.domain.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GameModeTest {

    @Test
    public void bottomUp_cardOrder() {
        var testee = new GameMode.BottomUp();

        var higherCard = testee.higherCard(
                new Card(Card.Color.CLUBS, Card.Suit.TEN),
                new Card(Card.Color.DIAMONDS, Card.Suit.KING)
        );

        assertThat(higherCard, equalTo(new Card(Card.Color.CLUBS, Card.Suit.TEN)));
    }

    @Test
    public void bottomUp_stichWinningCard() {
        var testee = new GameMode.BottomUp();

        var card1 = new Card(Card.Color.DIAMONDS, Card.Suit.TEN);
        var card2 = new Card(Card.Color.SPADES, Card.Suit.SIX);
        var card3 = new Card(Card.Color.DIAMONDS, Card.Suit.EIGHT);
        var card4 = new Card(Card.Color.DIAMONDS, Card.Suit.ACE);

        var tableStack = List.of(
                card1.play(),
                card2.play(),
                card3.play(),
                card4.play()
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card3.play("")));
    }

    @Test
    public void topDown_stichWinningCard() {
        var testee = new GameMode.TopDown();

        var card1 = new Card(Card.Color.DIAMONDS, Card.Suit.TEN);
        var card2 = new Card(Card.Color.SPADES, Card.Suit.SIX);
        var card3 = new Card(Card.Color.DIAMONDS, Card.Suit.EIGHT);
        var card4 = new Card(Card.Color.DIAMONDS, Card.Suit.ACE);

        var tableStack = List.of(
                card1.play(),
                card2.play(),
                card3.play(),
                card4.play()
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card4.play("")));
    }

    @Test
    public void trump_stichWinningCard_noTrump() {
        var testee = new GameMode.Trump(Card.Color.CLUBS);

        var card1 = new Card(Card.Color.DIAMONDS, Card.Suit.TEN);
        var card2 = new Card(Card.Color.SPADES, Card.Suit.SIX);
        var card3 = new Card(Card.Color.DIAMONDS, Card.Suit.EIGHT);
        var card4 = new Card(Card.Color.DIAMONDS, Card.Suit.ACE);

        var tableStack = List.of(
                card1.play(),
                card2.play(),
                card3.play(),
                card4.play()
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card4.play("")));
    }

    @Test
    public void trump_stichWinningCard_jack() {
        var testee = new GameMode.Trump(Card.Color.SPADES);

        var card1 = new Card(Card.Color.DIAMONDS, Card.Suit.TEN);
        var card2 = new Card(Card.Color.SPADES, Card.Suit.JACK);
        var card3 = new Card(Card.Color.DIAMONDS, Card.Suit.EIGHT);
        var card4 = new Card(Card.Color.DIAMONDS, Card.Suit.ACE);

        var tableStack = List.of(
                card1.play(),
                card2.play(),
                card3.play(),
                card4.play()
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card2.play("")));
    }

    @Test
    public void trump_stichWinningCard_nell() {
        var testee = new GameMode.Trump(Card.Color.SPADES);

        var card1 = new Card(Card.Color.DIAMONDS, Card.Suit.TEN);
        var card2 = new Card(Card.Color.SPADES, Card.Suit.ACE);
        var card3 = new Card(Card.Color.SPADES, Card.Suit.NINE);
        var card4 = new Card(Card.Color.DIAMONDS, Card.Suit.ACE);

        var tableStack = List.of(
                card1.play(),
                card2.play(),
                card3.play(),
                card4.play()
        );

        var winningCard = testee.winningCard(tableStack);

        assertThat(winningCard, equalTo(card3.play()));
    }
}