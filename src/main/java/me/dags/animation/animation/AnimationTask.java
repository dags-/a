package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */
public class AnimationTask implements Consumer<Task> {

    private final Animation animation;
    private final World world;
    private final Vector3i origin;

    private boolean complete = false;
    private int interval = 0;
    private int step = 0;

    private AnimationTask(Builder builder) {
        this.animation = builder.animation;
        this.world = builder.world;
        this.origin = builder.origin;
    }

    @Override
    public void accept(Task task) {
        if (--interval <= 0) {
            step++;
            interval = animation.play(world, origin);

            if (animation.hasFinished()) {
                complete = true;
                task.cancel();
            }
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public int getCurrentStep() {
        return step;
    }

    public void skip(int steps) {
        while (steps-- > 0 && !animation.hasFinished()) {
            interval = animation.play(world, origin);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Animation animation;
        private World world;
        private Vector3i origin;

        public Builder animation(Animation animation) {
            this.animation = animation;
            return this;
        }

        public Builder world(World world) {
            this.world = world;
            return this;
        }

        public Builder origin(Vector3i origin) {
            this.origin = origin;
            return this;
        }

        public AnimationTask build() {
            Preconditions.checkNotNull(animation);
            Preconditions.checkNotNull(world);
            Preconditions.checkNotNull(origin);
            return new AnimationTask(this);
        }
    }
}
