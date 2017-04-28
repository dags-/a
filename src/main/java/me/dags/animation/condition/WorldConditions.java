package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.util.Deserializers;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dags <dags@dags.me>
 */
public class WorldConditions {

    private final Map<String, Condition<Vector3i>> interactable = new ConcurrentHashMap<>();
    private final Map<String, Condition<Vector3i>> positional = new ConcurrentHashMap<>();
    private final ConditionRegistry registry;
    private final Path root;

    public WorldConditions(ConditionRegistry registry, Path dir) {
        this.registry = registry;
        this.root = dir;
    }

    public boolean hasRegistered(String id) {
        return interactable.containsKey(id) || positional.containsKey(id);
    }

    public void registerInteractable(Condition<Vector3i> condition) {
        registry.registerGlobal(condition);
        interactable.put(condition.getId(), condition);
    }

    public void registerPositional(Condition<Vector3i> condition) {
        registry.registerGlobal(condition);
        positional.put(condition.getId(), condition);
    }

    public Iterable<Condition<Vector3i>> getInteractable() {
        return interactable.values();
    }

    public Iterable<Condition<Vector3i>> getPositional() {
        return positional.values();
    }

    public Path getConditionsDir() {
        return root;
    }

    public void loadDefaults() {
        Deserializers.loadConditions(registry, getConditionsDir());
    }
}
