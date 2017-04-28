package me.dags.animation;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.command.Frames;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.condition.Condition;
import me.dags.animation.frame.FrameList;
import me.dags.animation.frame.FrameRecorder;
import me.dags.animation.handler.AnimationTask;
import me.dags.animation.handler.WorldHandlers;
import me.dags.animation.registry.AnimationRegistry;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.registry.FrameRegistry;
import me.dags.animation.registry.HandlerRegistry;
import me.dags.animation.util.Utils;
import me.dags.commandbus.CommandBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "animator", name = "animator", version = "0.1", description = "doot")
public class Animator {

    private static Animator instance;

    private final Cause cause;
    private final FrameRegistry frameRegistry;
    private final HandlerRegistry handlerRegistry;
    private final ConditionRegistry conditionRegistry;
    private final AnimationRegistry animationRegistry;
    private final Map<UUID, Aggregator> aggregators = new ConcurrentHashMap<>();
    private final Cache<UUID, FrameRecorder> frameRecorders = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();
    private final Cache<UUID, PositionRecorder> positionRecorders = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    @Inject
    public Animator(PluginContainer container, @ConfigDir(sharedRoot = false) Path dir) {
        cause = Cause.source(container).build();
        frameRegistry = new FrameRegistry(dir.resolve("frames"));
        handlerRegistry = new HandlerRegistry(dir.resolve("handlers"));
        conditionRegistry = new ConditionRegistry(dir.resolve("conditions"));
        animationRegistry = new AnimationRegistry(dir.resolve("animations"));
        instance = this;
    }

    @Listener
    public void preInit(GameInitializationEvent event) {
        Sponge.getRegistry().registerModule(FrameList.class, frameRegistry);
        Sponge.getRegistry().registerModule(Condition.class, conditionRegistry);
        Sponge.getRegistry().registerModule(AnimationFactory.class, animationRegistry);
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create().registerPackageOf(Frames.class).submit(this);
        EventHandler eventHandler = new EventHandler();
        Sponge.getEventManager().registerListeners(this, eventHandler);
        Task.builder().execute(eventHandler::process).intervalTicks(10).submit(this);
    }

    @Listener
    public void start(GameStartedServerEvent event) {
        reload(null);
    }

    @Listener
    public void reload(GameReloadEvent event) {
        aggregators.clear();
        handlerRegistry.clear();
        frameRecorders.invalidateAll();
        positionRecorders.invalidateAll();
        getFrameRegistry().registerDefaults();
        getConditionRegistry().registerDefaults();
        getAnimationRegistry().registerDefaults();
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

    public static FrameRegistry getFrameRegistry() {
        return instance.frameRegistry;
    }

    public static ConditionRegistry getConditionRegistry() {
        return instance.conditionRegistry;
    }

    public static AnimationRegistry getAnimationRegistry() {
        return instance.animationRegistry;
    }

    public static WorldHandlers getHandlers(World world) {
        return instance.handlerRegistry.getWorldHandlers(world.getName());
    }

    public static Task runAnimation(AnimationTask task) {
        return Task.builder().execute(task).intervalTicks(1L).submit(instance);
    }

    public static Optional<FrameRecorder> getFrameRecorder(UUID uuid) {
        return Optional.ofNullable(instance.frameRecorders.getIfPresent(uuid));
    }

    public static Optional<PositionRecorder> getPositionRecorder(UUID uuid) {
        return Optional.ofNullable(instance.positionRecorders.getIfPresent(uuid));
    }

    public static FrameRecorder createRecorder(UUID uuid, ItemType wand) {
        getFrameRecorder(uuid).ifPresent(recorder -> recorder.getTester().complete());
        FrameRecorder recorder = new FrameRecorder(wand);
        instance.frameRecorders.put(uuid, recorder);
        return recorder;
    }

    public static PositionRecorder createPositionRecorder(UUID uuid, ItemType wand) {
        PositionRecorder recorder = new PositionRecorder(wand);
        instance.positionRecorders.put(uuid, recorder);
        return recorder;
    }
}
