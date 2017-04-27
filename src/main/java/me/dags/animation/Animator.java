package me.dags.animation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.AnimationHandler;
import me.dags.animation.animation.AnimationTask;
import me.dags.animation.command.Frames;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.condition.Condition;
import me.dags.animation.condition.Keyword;
import me.dags.animation.frame.Recorder;
import me.dags.animation.registry.AnimationRegistry;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.commandbus.CommandBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "animator", name = "animator", version = "0.1", description = "doot")
public class Animator {

    private static Animator instance;

    private final Cause cause;
    private final AnimationRegistry animationRegistry = new AnimationRegistry();
    private final Map<UUID, Aggregator> aggregators = new ConcurrentHashMap<>();
    private final Map<String, List<AnimationHandler>> handlers = new HashMap<>();
    private final Map<String, ConditionRegistry> conditions = new ConcurrentHashMap<>();
    private final Cache<UUID, Recorder> recorders = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    @Inject
    public Animator(PluginContainer container) {
        cause = Cause.source(container).build();
        instance = this;
    }

    @Listener
    public void preInit(GameInitializationEvent event) {
        animationRegistry.registerDefaults();
        Sponge.getRegistry().registerModule(AnimationFactory.class, animationRegistry);
        Sponge.getRegistry().registerModule(Condition.class, new ConditionRegistry());
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create().registerPackageOf(Frames.class).submit(this);

        Sponge.getRegistry().register(Condition.class, new Keyword("tony", "tony"));

        EventHandler eventHandler = new EventHandler();
        Sponge.getEventManager().registerListeners(this, eventHandler);
        Task.builder().execute(eventHandler::process).intervalTicks(10).submit(this);
    }

    public static Cause getCause() {
        return instance.cause;
    }

    public static void dropAggregator(UUID uuid) {
        instance.aggregators.remove(uuid);
    }

    public static Aggregator getAggregator(UUID uuid) {
        return Utils.ensure(instance.aggregators, uuid, Aggregator::new);
    }

    public static ConditionRegistry getConditions(World world) {
        return Utils.ensure(instance.conditions, world.getName(), ConditionRegistry::new);
    }

    public static Iterable<AnimationHandler> getHandlers(World world) {
        return Utils.ensure(instance.handlers, world.getName(), LinkedList::new);
    }

    public static Task runAnimation(AnimationTask task) {
        return Task.builder().execute(task).intervalTicks(1L).submit(instance);
    }

    public static Optional<Recorder> getRecorder(UUID uuid) {
        return Optional.ofNullable(instance.recorders.getIfPresent(uuid));
    }

    public static Recorder createRecorder(UUID uuid, ItemType wand) {
        getRecorder(uuid).ifPresent(recorder -> recorder.getTester().complete());
        Recorder recorder = new Recorder(wand);
        instance.recorders.put(uuid, recorder);
        return recorder;
    }
}
