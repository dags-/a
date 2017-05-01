package me.dags.animation.registry;

import com.google.common.collect.ImmutableList;
import me.dags.animation.condition.Condition;
import me.dags.animation.condition.WorldConditions;
import me.dags.animation.util.Deserializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dags <dags@dags.me>
 */
public class ConditionRegistry implements CatalogRegistryModule<Condition> {

    private final Map<String, WorldConditions> managers = new ConcurrentHashMap<>();
    private final Map<String, Condition<String>> textual = new ConcurrentHashMap<>();
    private final Map<String, Condition<Subject>> permission = new ConcurrentHashMap<>();
    private final Map<String, Condition<?>> conditions = new ConcurrentHashMap<>();
    private final Path root;

    public ConditionRegistry(Path path) {
        this.root = path;
    }

    public WorldConditions getWorldConditions(String world) {
        WorldConditions conditions = managers.get(world);
        if (conditions == null) {
            managers.put(world, conditions = new WorldConditions(this, root.resolve(world)));
            conditions.loadDefaults();
        }
        return conditions;
    }

    public WorldConditions getWorldConditions(World world) {
        return getWorldConditions(world.getName());
    }

    public boolean hasRegisteredTextual(String id) {
        return textual.containsKey(id);
    }

    public boolean hasRegisteredPermission(String id) {
        return permission.containsKey(id);
    }

    public void registerGlobal(Condition<?> condition) {
        conditions.put(condition.getId(), condition);
    }

    public void registerTextual(Condition<String> condition) {
        textual.put(condition.getId(), condition);
    }

    public void registerPermission(Condition<Subject> condition) {
        permission.put(condition.getId(), condition);
    }

    public Iterable<Condition<String>> getTextual() {
        return textual.values();
    }

    public Iterable<Condition<Subject>> getPermission() {
        return permission.values();
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
        managers.clear();
        textual.clear();
        permission.clear();
        conditions.clear();
        Deserializers.loadConditions(this, getConditionsDir());
        Sponge.getServer().getWorlds().forEach(this::getWorldConditions);
    }

    public Path getConditionsDir() {
        return root;
    }
}
