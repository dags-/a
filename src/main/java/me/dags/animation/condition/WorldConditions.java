package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonElement;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.util.Deserializers;
import me.dags.animation.util.Utils;

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

    public boolean registerInteractable(Condition<Vector3i> condition) {
        if (registry.register(condition)) {
            interactable.put(condition.getId(), condition);
            return true;
        }
        return false;
    }

    public boolean registerPositional(Condition<Vector3i> condition) {
        if (registry.register(condition)) {
            positional.put(condition.getId(), condition);
            return true;
        }
        return false;
    }

    public Iterable<Condition<Vector3i>> getInteractable() {
        return interactable.values();
    }

    public Iterable<Condition<Vector3i>> getPositional() {
        return positional.values();
    }

    public void loadDefaults() {
        Utils.readDir(root)
                .filter(Utils::isJsonFile)
                .map(Utils::read)
                .filter(JsonElement::isJsonObject)
                .map(Deserializers::condition)
                .forEach(condition -> condition.register(registry));
    }
}
