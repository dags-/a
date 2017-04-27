package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.util.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class PushPullAnimation implements Animation {

    private final Animation animation;
    private boolean pull = false;

    public PushPullAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public Sequence<Frame> getTimeline() {
        return animation.getTimeline();
    }

    @Override
    public boolean hasFinished() {
        return pull && animation.hasFinished();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (animation.hasFinished()) {
            reset();
        }
        return animation.play(world, position);
    }

    @Override
    public void reset() {
        animation.getTimeline().reverse();
        pull = !pull;
    }

    public static class Factory implements AnimationFactory {

        @Override
        public Animation create(Animation animation) {
            return new PushPullAnimation(animation);
        }

        @Override
        public String getId() {
            return "push-pull";
        }

        @Override
        public String getName() {
            return getId();
        }
    }
}
