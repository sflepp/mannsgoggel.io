package io.mannsgoggel.gamejass.domain.game;

import io.mannsgoggel.gamejass.domain.action.Action;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class ActionHistory implements Serializable {
    private final List<Action<?>> actions;

    public ActionHistory add(Action<?> action) {
        return toBuilder()
                .actions(
                        Stream.concat(actions.stream(), Stream.of(action))
                                .collect(Collectors.toUnmodifiableList())
                )
                .build();
    }

    public Action<?> getLast() {
        return actions.get(actions.size() - 1);
    }

    public ActionHistory toPlayerView(String player) {
        return toBuilder()
                .actions(actions.stream()
                        .map(action -> action.toPlayerView(player))
                        .collect(Collectors.toUnmodifiableList())
                ).build();
    }
}
