package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.util.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class RepeatAnimation implements Animation {

    private final Animation animation;

    public RepeatAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public Sequence<Frame> getTimeline() {
        return animation.getTimeline();
    }

    @Listener
    public boolean hasFinished() {
        return false;
    }

    @Override
    public void reset() {
        animation.reset();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (animation.hasFinished()) {
            animation.reset();
        }
        return animation.play(world, position);
    }

    public static class Factory implements AnimationFactory {

        @Override
        public Animation create(Animation animation) {
            return new RepeatAnimation(animation);
        }

        @Override
        public String getId() {
            return "repeat";
        }

        @Override
        public String getName() {
            return getId();
        }
    }
}
