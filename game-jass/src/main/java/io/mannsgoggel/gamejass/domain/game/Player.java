package io.mannsgoggel.gamejass.domain.game;

import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private final String name;
    private List<Card> handCards;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public void removeHandCard(Card card) {
        handCards = handCards.stream()
                .filter(c -> !c.equals(card))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", handCards=" + handCards +
                '}';
    }
}
