package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import me.dags.animation.Animator;
import me.dags.animation.Sequence;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.condition.Condition;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class AnimationHandler {

    private final List<AnimationFactory> factories;
    private final List<Condition<?>> triggers;
    private final List<Frame> frames;
    private final Vector3i origin;

    private AnimationTask animationTask = null;
    private Task task = null;

    private AnimationHandler(Builder builder) {
        this.factories = ImmutableList.copyOf(builder.factories);
        this.triggers = ImmutableList.copyOf(builder.triggers);
        this.frames = builder.frames;
        this.origin = builder.origin;
    }

    public boolean isActive() {
        return animationTask == null || animationTask.isComplete();
    }

    public void process(World world, Aggregator active) {
        if (!isActive()) {
            if (active.containsAll(triggers)) {
                start(world);
            }
        }
    }

    public void start(World world) {
        if (animationTask == null || animationTask.isComplete()) {
            Animation animation = new SimpleAnimation(Sequence.of(frames));

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

    public void pause() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void resume() {
        if (animationTask != null && !animationTask.isComplete()) {
            task = Animator.runAnimation(animationTask);
        }
    }

    public void complete() {
        if (animationTask != null) {
            pause();
            animationTask = null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<AnimationFactory> factories = new LinkedList<>();
        private List<Condition<?>> triggers = new LinkedList<>();
        private List<Frame> frames = Collections.emptyList();
        private Vector3i origin = Vector3i.ZERO;

        public Builder factories(Collection<AnimationFactory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        public Builder factory(AnimationFactory factory) {
            factories.add(factory);
            return this;
        }

        public Builder trigger(Condition<?> trigger) {
            triggers.add(trigger);
            return this;
        }

        public Builder frames(List<Frame> frames) {
            this.frames = frames;
            return this;
        }

        public Builder origin(Vector3i origin) {
            this.origin = origin;
            return this;
        }

        public AnimationHandler build() {
            return new AnimationHandler(this);
        }
    }
}
