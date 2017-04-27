package me.dags.animation.registry;

import com.google.common.collect.ImmutableList;
import me.dags.animation.condition.Condition;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dags <dags@dags.me>
 */
public class ConditionRegistry implements CatalogRegistryModule<Condition> {

    private static final Map<String, Condition<?>> conditions = new ConcurrentHashMap<>();

    private final Map<String, Condition<Location<World>>> interactable = new ConcurrentHashMap<>();
    private final Map<String, Condition<Location<World>>> positional = new ConcurrentHashMap<>();
    private final Map<String, Condition<String>> textual = new ConcurrentHashMap<>();

    public boolean registerInteractable(Condition<Location<World>> condition) {
        if (!conditions.containsKey(condition.getName())) {
            conditions.put(condition.getId(), condition);
            interactable.put(condition.getId(), condition);
            return true;
        }
        return false;
    }

    public boolean registerPositional(Condition<Location<World>> condition) {
        if (!conditions.containsKey(condition.getName())) {
            conditions.put(condition.getId(), condition);
            positional.put(condition.getId(), condition);
            return true;
        }
        return false;
    }

    public boolean registerTextual(Condition<String> condition) {
        if (!conditions.containsKey(condition.getName())) {
            conditions.put(condition.getId(), condition);
            textual.put(condition.getId(), condition);
            return true;
        }
        return false;
    }
    public Collection<Condition<Location<World>>> getInteractable() {
        return ImmutableList.copyOf(interactable.values());
    }

    public Collection<Condition<Location<World>>> getPositional() {
        return ImmutableList.copyOf(positional.values());
    }

    public Collection<Condition<String>> getTextual() {
        return ImmutableList.copyOf(textual.values());
    }

    @Override
    public Optional<Condition> getById(String id) {
        return Optional.ofNullable(conditions.get(id));
    }

    @Override
    public Collection<Condition> getAll() {
        return ImmutableList.copyOf(conditions.values());
    }
}
