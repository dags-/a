package me.dags.animation.context;

import org.spongepowered.api.CatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface Condition<T> extends CatalogType {

    boolean test(T t);

    default String getId() {
        return getName();
    }
}
