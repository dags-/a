package me.dags.animation.condition;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author dags <dags@dags.me>
 */
public class Aggregator {

    private final Set<Condition<?>> active = Collections.newSetFromMap(Maps.newConcurrentMap());

    public void add(Condition<?> condition) {
        active.add(condition);
    }

    public boolean containsAll(Collection<Condition<?>> test) {
        return active.containsAll(test);
    }

    public void reset() {
        active.clear();
    }
}