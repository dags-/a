package me.dags.animation;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Function;

/**
 * @author dags <dags@dags.me>
 */
public class Sequence<T> {

    private final List<T> elements;
    private final int start = -1;
    private final int end;
    private int direction = 1;
    private int pos = -1;

    public Sequence(List<T> list) {
        this.elements = list;
        this.end = elements.size();
    }

    private List<T> getElements() {
        return elements;
    }

    public boolean hasNext() {
        int next = pos + direction;
        return next > -1 && next < getElements().size();
    }

    public T current() {
        if (pos == -1) {
            return getElements().get(0);
        }
        if (pos == getElements().size()) {
            return getElements().get(getElements().size() - 1);
        }
        if (pos > -1 && pos < getElements().size()) {
            return getElements().get(pos);
        }
        return null;
    }

    public T next() {
        if (hasNext()) {
            pos += direction;
            return getElements().get(pos);
        }
        return current();
    }

    public Sequence<T> setDirection(int dir) {
        direction = dir < 0 ? -1 : 1;
        return this;
    }

    public Sequence<T> skip() {
        if (pos > start && pos < end) {
            pos += direction;
        }
        return this;
    }

    public Sequence<T> reset() {
        return direction == 1 ? goToStart() : goToEnd();
    }

    public Sequence<T> reverse() {
        direction = -direction;
        return this;
    }

    public Sequence<T> goToStart() {
        pos = -1;
        return this;
    }

    public Sequence<T> goToEnd() {
        pos = getElements().size();
        return this;
    }

    public static <T> Sequence<T> of(Iterable<T> iterable) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (T t : iterable) {
            builder.add(t);
        }
        return new Sequence<>(builder.build());
    }

    public static <S, T> Sequence<T> of(Iterable<S> iterable, Function<S, T> mapper) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (S s : iterable) {
            builder.add(mapper.apply(s));
        }
        return new Sequence<>(builder.build());
    }
}
