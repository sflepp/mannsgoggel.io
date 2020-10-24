package io.mannsgoggel.gamejass.domain;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toUnmodifiableList;

public class CollectionShortcuts {
    public static <T, U> List<U> map(List<T> list, Function<? super T, ? extends U> mapper) {
        return list.stream().map(mapper).collect(toUnmodifiableList());
    }

    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        return list.stream().filter(predicate).collect(toUnmodifiableList());
    }

    public static <T> T any(List<T> list, Predicate<? super T> predicate) {
        return list.stream().filter(predicate).findAny().orElseThrow();
    }
}
