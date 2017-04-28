package me.dags.animation.registry;

import me.dags.animation.handler.WorldHandlers;
import me.dags.animation.util.Utils;

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

    public WorldHandlers getWorldHandlers(String world) {
        return Utils.ensure(handlers, world, supplier(world));
    }

    public void clear() {
        handlers.clear();
    }

    private Supplier<WorldHandlers> supplier(String world) {
        return () -> {
            WorldHandlers handlers = new WorldHandlers(root.resolve(world));
            handlers.registerDefaults();
            return handlers;
        };
    }
}
