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

    private final Sequence<Frame> timeline;
    private Frame.History history = null;

    public SimpleAnimation(Sequence<Frame> timeline) {
        this.timeline = timeline;
    }

    @Override
    public Sequence<Frame> getTimeline() {
        return timeline;
    }

    @Override
    public boolean hasFinished() {
        return !timeline.hasNext();
    }

    @Override
    public void reset() {
        timeline.setDirection(1);
        timeline.reset();
    }

    @Override
    public void reverse() {
        timeline.skip().reverse();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (history != null) {
            history.apply(world, position, BlockChangeFlag.NONE);
        }

        Frame frame = timeline.next();
        history = frame.apply(world, position, BlockChangeFlag.NONE);

        return frame.getDuration();
    }
}
