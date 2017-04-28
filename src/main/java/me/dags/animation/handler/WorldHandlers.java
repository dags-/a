package me.dags.animation.handler;

import me.dags.animation.util.Deserializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class WorldHandlers {

    private final Map<String, AnimationHandler> handlers = new HashMap<>();
    private final Path root;

    public WorldHandlers(Path dir) {
        this.root = dir;
    }

    public void delete(String id) {
        if (handlers.containsKey(id)) {
            AnimationHandler handler = handlers.remove(id);
            Path path = root.resolve(handler.getFileName());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(AnimationHandler handler) {
        handlers.put(handler.getId(), handler);
    }

    public Optional<AnimationHandler> getById(String id) {
        return Optional.ofNullable(handlers.get(id));
    }

    public Iterable<AnimationHandler> getAll() {
        return handlers.values();
    }

    public void registerDefaults() {
        handlers.clear();
        Deserializers.loadHandlers(this, root);
    }
}
