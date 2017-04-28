package me.dags.animation.condition;

import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.registry.JsonCatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface Condition<T> extends JsonCatalogType {

    boolean test(T t);

    void register(ConditionRegistry registry);
}
