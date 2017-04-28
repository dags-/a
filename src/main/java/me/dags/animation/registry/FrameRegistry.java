package me.dags.animation.registry;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import me.dags.animation.frame.Frame;
import me.dags.animation.frame.FrameList;
import me.dags.animation.frame.SchemFrame;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
public class FrameRegistry implements CatalogRegistryModule<FrameList>, CacheLoader<FrameList, List<Frame>> {

    private final Map<String, FrameList> registry = new HashMap<>();
    private final LoadingCache<FrameList, List<Frame>> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(this);

    private final Path root;

    public FrameRegistry(Path path) {
        this.root = path;
    }

    public List<Frame> getFrames(FrameList key) {
        return cache.get(key);
    }

    @Override
    public Optional<FrameList> getById(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    @Override
    public Collection<FrameList> getAll() {
        return ImmutableList.copyOf(registry.values());
    }

    @Override
    public List<Frame> load(FrameList key) throws Exception {
        Path path = root.resolve(key.getId() + ".frames");
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path)) {
                List<Frame> frames = SchemFrame.readAll(inputStream);
                registry.put(key.getId(), key);
                return frames;
            }
        }
        return Collections.emptyList();
    }
}
