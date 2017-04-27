package me.dags.animation.animation;

import org.spongepowered.api.CatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface AnimationFactory extends CatalogType {

    Animation create(Animation animation);
}
