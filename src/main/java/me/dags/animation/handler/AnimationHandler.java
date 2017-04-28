package me.dags.animation.handler;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import me.dags.animation.Animator;
import me.dags.animation.animation.Animation;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.SimpleAnimation;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.condition.Condition;
import me.dags.animation.frame.Frame;
import me.dags.animation.registry.JsonCatalogType;
import me.dags.animation.util.SequenceProvider;
import me.dags.animation.util.Serializers;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class AnimationHandler implements JsonCatalogType {

    private final String name;
    private final String world;
    private final Vector3i origin;
    private final SequenceProvider<Frame> frames;
    private final List<AnimationFactory> factories;
    private final List<List<Condition<?>>> triggers;

    private AnimationTask animationTask = null;
    private Task task = null;

    private AnimationHandler(Builder builder) {
        this.factories = ImmutableList.copyOf(builder.factories);
        this.triggers = ImmutableList.copyOf(builder.triggers);
        this.frames = builder.frames;
        this.origin = builder.origin;
        this.world = builder.world;
        this.name = builder.name;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public Vector3i getOrigin() {
        return origin;
    }

    public SequenceProvider<Frame> getFrames() {
        return frames;
    }

    public List<AnimationFactory> getFactories() {
        return factories;
    }

    public List<List<Condition<?>>> getTriggers() {
        return triggers;
    }

    public boolean isActive() {
        return animationTask != null && (animationTask.isPaused() || !animationTask.isComplete());
    }

    public void process(World world, Aggregator active) {
        for (List<Condition<?>> trigger : triggers) {
            if (active.containsAll(trigger)) {
                start(world);
                return;
            }
        }
    }

    public void start(World world) {
        if (animationTask != null) {
            if (animationTask.isPaused()) {
                animationTask.setPaused(false);
                return;
            }

            if (animationTask.isComplete()) {
                animationTask = null;
            }
        }

        if (animationTask == null) {
            Animation animation = new SimpleAnimation(frames.getSequence());

            for (AnimationFactory factory : factories) {
                animation = factory.create(animation);
            }

            animationTask = AnimationTask.builder()
                    .animation(animation)
                    .origin(origin)
                    .world(world)
                    .build();

            task = Animator.runAnimation(animationTask);
        }
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (animationTask != null) {
            animationTask = null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getType() {
        return "handler";
    }

    @Override
    public void populate(JsonObject object) {
        object.addProperty("world", getWorld());
        object.addProperty("frames", getFrames().getId());
        object.add("origin", Serializers.vector(getOrigin()));
        object.add("animations", Serializers.animations(getFactories()));
        object.add("triggers", Serializers.triggers(getTriggers()));
    }

    public static class Builder {

        private String name;
        private String world;
        private Vector3i origin = null;
        private SequenceProvider<Frame> frames = null;
        private List<AnimationFactory> factories = new LinkedList<>();
        private List<List<Condition<?>>> triggers = new LinkedList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder world(String world) {
            this.world = world;
            return this;
        }

        public Builder animations(Collection<AnimationFactory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        public Builder animation(AnimationFactory factory) {
            factories.add(factory);
            return this;
        }

        public Builder triggers(List<List<Condition<?>>> triggers) {
            for (List<Condition<?>> conditions : triggers) {
                trigger(conditions);
            }
            return this;
        }

        public Builder trigger(List<Condition<?>> conditions) {
            if (!conditions.isEmpty()) {
                triggers.add(conditions);
            }
            return this;
        }

        public Builder trigger(Condition<?> trigger) {
            triggers.add(Collections.singletonList(trigger));
            return this;
        }

        public Builder sequenceProvider(SequenceProvider<Frame> frames) {
            this.frames = frames;
            return this;
        }

        public Builder origin(Vector3i origin) {
            this.origin = origin;
            return this;
        }

        public AnimationHandler build() {
            AnimationHandler handler = new AnimationHandler(this);
            name = null;
            world = null;
            frames = null;
            triggers = null;
            factories = null;
            return handler;
        }
    }
}
