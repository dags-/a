package me.dags.animation.animation;

import me.dags.animation.registry.JsonCatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface AnimationFactory extends JsonCatalogType {

    Animation create(Animation animation);
}
