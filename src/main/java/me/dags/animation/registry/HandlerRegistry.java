package me.dags.animation.registry;

import me.dags.animation.handler.WorldHandlers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
        WorldHandlers worldHandlers = handlers.get(world);
        if (worldHandlers == null) {
            handlers.put(world, worldHandlers = new WorldHandlers(root.resolve(world)));
            worldHandlers.registerDefaults();
        }
        return worldHandlers;
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
}
