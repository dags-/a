package me.dags.animation.context;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public class ConditionManager {

    private final Map<String, Condition<Location<World>>> interactable = new HashMap<>();
    private final Map<String, Condition<Location<World>>> positional = new HashMap<>();
    private final Map<String, Condition<String>> textual = new HashMap<>();

    public Context registerInteractable(Condition<Location<World>> condition) {
        Context context = new Context("interaction", condition.getId());
        interactable.put(condition.getId(), condition);
        return context;
    }

    public Context registerPositional(Condition<Location<World>> condition) {
        Context context = new Context("position", condition.getId());
        positional.put(condition.getId(), condition);
        return context;
    }

    public Context registerTextual(Condition<String> condition) {
        Context context = new Context("text", condition.getId());
        textual.put(condition.getId(), condition);
        return context;
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
}
