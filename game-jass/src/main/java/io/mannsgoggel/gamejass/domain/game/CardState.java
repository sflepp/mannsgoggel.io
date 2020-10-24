package io.mannsgoggel.gamejass.domain.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardState implements Serializable {
    Card card;
    String player;
    Integer tableOrder;
    String team;

    public CardState toPlayerView(String player) {
        if (this.player == null || this.tableOrder != null || this.player.equals(player)) {
            return this;
        } else {
            return toBuilder().player("").build();
        }
    }

    public boolean queryIsOnPlayer() {
        return tableOrder == null && team == null;
    }

    public CardState play(Integer index) {
        return this.toBuilder().tableOrder(index).build();
    }

    public boolean queryIsOnTable() {
        return tableOrder != null && team == null;
    }

    public CardState moveToTeam(String team) {
        return this.toBuilder().team(team).build();
    }
}
