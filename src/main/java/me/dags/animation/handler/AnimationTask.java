package me.dags.animation.handler;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import me.dags.animation.animation.Animation;
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

    private boolean paused = false;
    private boolean complete = false;
    private int interval = 0;

    private AnimationTask(Builder builder) {
        this.animation = builder.animation;
        this.world = builder.world;
        this.origin = builder.origin;
    }

    @Override
    public void accept(Task task) {
        if (!paused && --interval <= 0) {
            interval = animation.play(world, origin);
            paused = interval < 1;

            if (animation.hasFinished()) {
                complete = true;
                paused = false;
                task.cancel();
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
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
