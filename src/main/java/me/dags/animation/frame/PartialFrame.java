package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 *
 * Contains only the blocks that will be changed
 */
public class PartialFrame implements Frame {

    private final List<BlockSnapshot> changes = ImmutableList.of();
    private final int duration;

    public PartialFrame(int duration) {
        this.duration = duration;
    }

    @Override
    public Instance newInstance() {
        return new PartialInstance();
    }

    private class PartialInstance implements Frame.Instance {

        private final List<BlockSnapshot> history = new LinkedList<>();

        @Override
        public int getDuration() {
            return duration;
        }

        @Override
        public void paste(World world, Vector3i position, BlockChangeFlag flag) {
            for (BlockSnapshot block : changes) {
                Vector3i pos = block.getPosition().add(position);
                BlockSnapshot current = world.createSnapshot(pos);
                history.add(current);
                block.restore(true, flag);
            }
        }

        @Override
        public void reset(World world, Vector3i position, BlockChangeFlag flag) {
            for (BlockSnapshot block : history) {
                block.restore(true, flag);
            }
            history.clear();
        }
    }
}
