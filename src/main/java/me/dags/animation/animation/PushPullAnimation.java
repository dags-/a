package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
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
    public Sequence<Frame.Instance> getTimeline() {
        return animation.getTimeline();
    }

    @Override
    public boolean hasFinished() {
        return pull && animation.hasFinished();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (getTimeline().hasNext()) {
            return animation.play(world, position);
        } else {
            pull = !pull;
            getTimeline().reverse();
            return animation.play(world, position);
        }
    }

    @Override
    public int playFrame(World world, Vector3i position) {
        return animation.playFrame(world, position);
    }
}
