package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public interface Animation {

    Sequence<Frame> getTimeline();

    boolean hasFinished();

    void reset();

    void reverse();

    int play(World world, Vector3i position);
}
