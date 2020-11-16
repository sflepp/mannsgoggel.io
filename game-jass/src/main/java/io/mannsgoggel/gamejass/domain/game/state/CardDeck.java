package io.mannsgoggel.gamejass.domain.game.state;

import io.mannsgoggel.gamejass.domain.game.GameModes;
import io.mannsgoggel.gamejass.domain.game.JassRules;

import java.util.*;

import static io.mannsgoggel.gamejass.domain.CollectionShortcuts.map;
import static io.mannsgoggel.gamejass.domain.game.state.State.*;
import static java.util.stream.Collectors.toUnmodifiableList;

public class CardDeck {

    public static List<CardState> buildInitial() {
        return map(build(), card -> CardState.builder().card(card).build());
    }

    public static List<Card> build() {
        List<Card> cards = new ArrayList<>();

        List.of(Card.Color.values()).forEach(color -> {
            Set.of(Card.Suit.values()).forEach(suit -> {
                cards.add(new Card(color, suit));
            });
        });

        return cards;
    }

    public static List<CardState> buildAndShuffle(List<String> players, GameModes.GameMode.PlayingMode mode) {
        List<Card> cards = CardDeck.build();

        Collections.shuffle(cards);

        List<CardState> cardStates = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            for (int j = i * 9; j < (i + 1) * 9; j++) {
                var card = cards.get(j);
                var player = players.get(i);

                var cardState = CardState.builder()
                        .card(card)
                        .player(player);


                if (mode != null) {
                    cardState
                            .points(JassRules.cardPoints(mode, card))
                            .isTrump(JassRules.isTrump(mode, card));
                }

                cardStates.add(cardState.build());
            }
        }

        return cardStates.stream()
                .sorted(Comparator.comparingInt(a -> a.getCard().getSuit().ordinal()))
                .sorted(Comparator.comparingInt(a -> a.getCard().getColor().ordinal()))
                .collect(toUnmodifiableList());
    }
}
