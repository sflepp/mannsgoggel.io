package com.manoggeli.gamejass.domain.gameObjects;

import java.util.Set;

public class Player {
    private final String name;
    private Set<Card> handCards;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(Set<Card> handCards) {
        this.handCards = handCards;
    }
}
