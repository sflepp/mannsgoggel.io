package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class ActionHistory {
    private List<Action> actions = new ArrayList<>();

    public void add(Action action) {
        actions.add(action);
    }

    public Action getLast() {
        return actions.get(actions.size() - 1);
    }

    public ActionHistory toPlayerView(String player) {
        var history =  new ActionHistory();
        history.setActions(actions.stream()
                .map(action -> action.toPlayerView(player))
                .collect(Collectors.toUnmodifiableList())
        );
        return history;
    }
}
