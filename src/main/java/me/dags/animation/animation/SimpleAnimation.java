package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class SimpleAnimation implements Animation {

    private final Sequence<Frame.Instance> timeline;

    private Frame.Instance lastFrame = null;
    private Vector3i lastPosition = null;

    public SimpleAnimation(Sequence<Frame.Instance> timeline) {
        this.timeline = timeline;
    }

    @Override
    public Sequence<Frame.Instance> getTimeline() {
        return timeline;
    }

    @Override
    public int playFrame(World world, Vector3i position) {
        if (lastFrame != null) {
            lastFrame.reset(world, lastPosition, BlockChangeFlag.NONE);
        }

        lastFrame = timeline.next();
        lastPosition = position;
        lastFrame.paste(world, position, BlockChangeFlag.NONE);

        return lastFrame.getDuration();
    }
}
