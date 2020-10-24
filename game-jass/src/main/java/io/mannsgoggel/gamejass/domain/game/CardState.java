package io.mannsgoggel.gamejass.domain.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardState {
    Card card;
    String player;
    Integer playOrder;
    String team;
    Boolean isTrump;
    Integer points;

    public CardState toPlayerView(String player) {
        if (this.player == null || this.playOrder != null || this.player.equals(player)) {
            return this;
        } else {
            return toBuilder().player("").build();
        }
    }

    public boolean queryIsOnPlayer() {
        return playOrder == null && team == null;
    }

    public CardState play(Integer index) {
        return this.toBuilder().playOrder(index).build();
    }

    public boolean queryIsOnTable() {
        return playOrder != null && team == null;
    }
}
