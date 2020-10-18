package com.manoggeli.gamejass.domain.gameObjects;

public class Card implements Comparable<Card> {
    public enum Color {
        HEARTHS, SPADES, DIAMONDS, CLUBS
    }

    public enum Suit {
        ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX
    }

    private final Color color;
    private final Suit suit;
    private boolean trump;
    private Integer rank;
    private Integer points;

    public Card(Color color, Suit suit) {
        this.color = color;
        this.suit = suit;
    }

    public Color getColor() {
        return color;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean isTrump() {
        return trump;
    }

    public Integer getRank() {
        return rank;
    }

    public Integer getPoints() {
        return points;
    }

    public void setTrump(boolean trump) {
        this.trump = trump;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public int compareTo(Card other) {
        return rank - other.getRank();
    }

    public boolean isHigherThan(Card other) {
        return this.compareTo(other) > 0;
    }
}
