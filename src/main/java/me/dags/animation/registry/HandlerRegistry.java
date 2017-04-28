package me.dags.animation.registry;

import me.dags.animation.handler.WorldHandlers;
import me.dags.animation.util.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author dags <dags@dags.me>
 */
public class HandlerRegistry {

    private final Map<String, WorldHandlers> handlers = new HashMap<>();
    private final Path root;

    public HandlerRegistry(Path dir) {
        this.root = dir;
    }

    public WorldHandlers getWorldHandlers(World world) {
        return getWorldHandlers(world.getName());
    }

    public WorldHandlers getWorldHandlers(String world) {
        return Utils.ensure(handlers, world, supplier(world));
    }

    public void clear() {
        handlers.values().forEach(worldHandlers -> worldHandlers.getAll().forEach(handler -> {
            if (handler.isActive()) {
                handler.cancel();
            }
        }));

        handlers.clear();
    }

    public void registerDefaults() {
        clear();
        Sponge.getServer().getWorlds().forEach(this::getWorldHandlers);
    }

    private Supplier<WorldHandlers> supplier(String world) {
        return () -> {
            WorldHandlers handlers = new WorldHandlers(root.resolve(world));
            handlers.registerDefaults();
            return handlers;
        };
    }
}
