package me.dags.animation.registry;

import com.google.common.collect.ImmutableList;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.PushPullAnimation;
import me.dags.animation.animation.RepeatAnimation;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class AnimationRegistry implements CatalogRegistryModule<AnimationFactory> {

    private final Map<String, AnimationFactory> factories = new HashMap<>();

    public boolean register(AnimationFactory factory) {
        if (!factories.containsKey(factory.getId())) {
            factories.put(factory.getId(), factory);
            return true;
        }
        return false;
    }

    @Override
    public Optional<AnimationFactory> getById(String id) {
        return Optional.ofNullable(factories.get(id));
    }

    @Override
    public Collection<AnimationFactory> getAll() {
        return ImmutableList.copyOf(factories.values());
    }

    @Override
    public void registerDefaults() {
        register(new PushPullAnimation.Factory());
        register(new RepeatAnimation.Factory());
    }
}
