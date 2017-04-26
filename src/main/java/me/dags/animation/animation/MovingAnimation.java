package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class MovingAnimation implements Animation {

    private final Animation animation;
    private final Sequence<Vector3i> positions;

    private Vector3i offset = new Vector3i(0, 0, 0);

    public MovingAnimation(Animation animation, Sequence<Vector3i> positions) {
        this.animation = animation;
        this.positions = positions;
    }

    @Override
    public Sequence<Frame> getTimeline() {
        return animation.getTimeline();
    }

    @Override
    public boolean hasFinished() {
        return !positions.hasNext() || animation.hasFinished();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (positions.hasNext()) {
            offset = offset.add(positions.next());
            Vector3i pos = position.add(offset);
            return animation.play(world, pos);
        }
        return 0;
    }

    @Override
    public void reset() {
        animation.reset();
        positions.goToStart();
    }
}
