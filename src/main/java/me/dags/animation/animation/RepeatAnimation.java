package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
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
    public Sequence<Frame.Instance> getTimeline() {
        return animation.getTimeline();
    }

    @Listener
    public boolean hasFinished() {
        return false;
    }

    @Override
    public int play(World world, Vector3i position) {
        if (!animation.hasFinished()) {
            return animation.play(world, position);
        } else {
            getTimeline().goToStart();
            return animation.play(world, position);
        }
    }

    @Override
    public int playFrame(World world, Vector3i position) {
        return animation.playFrame(world, position);
    }
}
