package me.dags.animation.registry;

import com.google.common.collect.ImmutableList;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.PushPullAnimation;
import me.dags.animation.animation.RepeatAnimation;
import me.dags.animation.util.Deserializers;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class AnimationRegistry implements CatalogRegistryModule<AnimationFactory> {

    private final Map<String, AnimationFactory> factories = new HashMap<>();
    private final Path root;

    public AnimationRegistry(Path dir) {
        this.root = dir;
    }

    public boolean hasRegistered(String id) {
        return factories.containsKey(id);
    }

    public void register(AnimationFactory factory) {
        factories.put(factory.getId(), factory);
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
        factories.clear();
        register(new PushPullAnimation.Factory());
        register(new RepeatAnimation.Factory());
        Deserializers.loadAnimations(this, getAnimationsDir());
    }

    public Path getAnimationsDir() {
        return root;
    }
}
