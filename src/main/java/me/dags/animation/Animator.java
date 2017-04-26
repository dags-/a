package me.dags.animation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import me.dags.animation.animation.AnimationTask;
import me.dags.animation.command.Frames;
import me.dags.animation.frame.Recorder;
import me.dags.commandbus.CommandBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "animator", name = "animator", version = "0.1", description = "doot")
public class Animator {

    private static Cause cause;
    private static Animator instance;

    private static final Cache<UUID, Recorder> recorders = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    @Inject
    public Animator(PluginContainer container) {
        cause = Cause.source(container).build();
        instance = this;
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create().registerPackageOf(Frames.class).submit(this);
        Sponge.getEventManager().registerListeners(this, new EventHandler());
    }

    public static Cause getCause() {
        return cause;
    }

    public static Task runAnimation(AnimationTask task) {
        return Task.builder().execute(task).intervalTicks(1L).submit(instance);
    }

    public static Optional<Recorder> getRecorder(UUID uuid) {
        return Optional.ofNullable(recorders.getIfPresent(uuid));
    }

    public static Recorder createRecorder(UUID uuid, ItemType wand) {
        Recorder recorder = new Recorder(wand);
        recorders.put(uuid, recorder);
        return recorder;
    }
}
