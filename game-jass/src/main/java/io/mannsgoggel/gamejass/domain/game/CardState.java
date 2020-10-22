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
    Integer played;
    String team;

    public CardState toPlayerView(String player) {
        if(this.player == null || this.played != null || this.player.equals(player))  {
            return this;
        } else {
            return toBuilder().player("").build();
        }
    }

    public boolean queryIsOnPlayer() {
        return played == null && team == null;
    }

    public CardState play(Integer index) {
        return this.toBuilder().played(index).build();
    }

    public boolean queryIsOnTable() {
        return played != null && team == null;
    }

    public CardState moveToTeam(String team) {
        return this.toBuilder().team(team).build();
    }
}
