package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.frame.Frame;
import me.dags.animation.util.Sequence;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author dags <dags@dags.me>
 */
public class SimpleAnimation implements Animation {

    private static final Predicate<Entity> LIVING = Living.class::isInstance;

    private final Sequence<Frame> timeline;
    private Frame.History history = null;
    private Vector3i origin = null;

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
        timeline.reset();
        history = null;
        origin = null;
    }

    @Override
    public int play(World world, Vector3i position) {
        if (history != null) {
            moveLiving(world, history.getMin(), history.getMax(), position);
            history.apply(world, position, BlockChangeFlag.NONE);
        }
        Frame frame = timeline.next();
        history = frame.apply(world, position, BlockChangeFlag.NONE);
        origin = position;
        return frame.getDuration();
    }

    private void moveLiving(World world, Vector3i min, Vector3i max, Vector3i pos) {
        if (origin != null && !origin.equals(pos)) {
            Collection<Entity> entities = world.getExtentView(min, max).getEntities(LIVING);
            Vector3i offset = pos.sub(origin);
            for (Entity entity : entities) {
                entity.setLocation(entity.getLocation().add(offset));
            }
        }
    }
}
