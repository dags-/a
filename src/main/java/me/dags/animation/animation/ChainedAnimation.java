package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class ChainedAnimation implements Animation {

    private final Sequence<Animation> sequence;
    private Animation current = null;

    public ChainedAnimation(Sequence<Animation> sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean hasFinished() {
        if (current != null) {
            return current.hasFinished() && !sequence.hasNext();
        }
        return !sequence.hasNext();
    }

    @Override
    public Sequence<Frame> getTimeline() {
        if (current == null || current.hasFinished()) {
            if (sequence.hasNext()) {
                current = sequence.next();
                return current.getTimeline();
            }

            throw new UnsupportedOperationException("Unexpected end of chain");
        }

        return current.getTimeline();
    }

    @Override
    public int playFrame(World world, Vector3i position) {
        if (current != null) {
            return current.playFrame(world, position);
        }
        throw new UnsupportedOperationException("Unexpected end of chain");
    }
}
