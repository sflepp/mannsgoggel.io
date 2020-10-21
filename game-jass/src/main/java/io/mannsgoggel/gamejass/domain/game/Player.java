package io.mannsgoggel.gamejass.domain.game;

import lombok.Data;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Setter
public class Player {
    private final String name;
    private List<Card> handCards;

    public void removeHandCard(Card card) {
        handCards = handCards.stream()
                .filter(c -> !c.equals(card))
                .collect(Collectors.toList());
    }

    public Player toPlayerView(String player) {
        var playerView = new Player(name)
                ;
        playerView.setHandCards(handCards
                .stream()
                .map(card -> name.equals(player) ? card : card.hide())
                .collect(Collectors.toUnmodifiableList())
        );

        return playerView;
    }
}
