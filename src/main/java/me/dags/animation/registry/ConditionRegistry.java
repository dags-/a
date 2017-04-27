package me.dags.animation.registry;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import me.dags.animation.condition.Condition;
import me.dags.animation.condition.WorldConditions;
import me.dags.animation.util.Deserializers;
import me.dags.animation.util.Utils;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author dags <dags@dags.me>
 */
public class ConditionRegistry implements CatalogRegistryModule<Condition> {

    private final Map<String, WorldConditions> managers = new ConcurrentHashMap<>();
    private final Map<String, Condition<String>> textual = new ConcurrentHashMap<>();
    private final Map<String, Condition<?>> conditions = new ConcurrentHashMap<>();
    private final Path root;

    public ConditionRegistry(Path path) {
        this.root = path;
    }

    public WorldConditions getWorldConditions(String world) {
        return Utils.ensure(managers, world, newManager(world));
    }

    public WorldConditions getWorldConditions(World world) {
        return getWorldConditions(world.getName());
    }

    public boolean register(Condition<?> condition) {
        if (!conditions.containsKey(condition.getId())) {
            conditions.put(condition.getId(), condition);
            return true;
        }
        return false;
    }

    public void registerTextual(Condition<String> condition) {
        if (register(condition)) {
            textual.put(condition.getId(), condition);
        }
    }

    public Iterable<Condition<String>> getTextual() {
        return textual.values();
    }

    public Optional<Condition<?>> getCondition(String id) {
        return Optional.ofNullable(conditions.get(id));
    }

    @Override
    public Optional<Condition> getById(String id) {
        return Optional.ofNullable(conditions.get(id));
    }

    @Override
    public Collection<Condition> getAll() {
        return ImmutableList.copyOf(conditions.values());
    }

    @Override
    public void registerDefaults() {
        Utils.readDir(root)
                .filter(Utils::isJsonFile)
                .map(Utils::read)
                .filter(JsonElement::isJsonObject)
                .map(Deserializers::keyword)
                .filter(Objects::nonNull)
                .forEach(keyword -> keyword.register(this));
    }

    private Supplier<WorldConditions> newManager(String world) {
        return () -> {
            WorldConditions conditions = new WorldConditions(this, root.resolve(world));
            conditions.loadDefaults();
            return conditions;
        };
    }
}
