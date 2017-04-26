package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public interface Animation {

    Sequence<Frame> getTimeline();

    default boolean hasFinished() {
        return !getTimeline().hasNext();
    }

    default int play(World world, Vector3i position) {
        if (getTimeline().hasNext()) {
            return playFrame(world, position);
        }
        return 0;
    }

    int playFrame(World world, Vector3i position);
}
