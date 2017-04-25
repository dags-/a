package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.schematic.Schematic;

/**
 * @author dags <dags@dags.me>
 *
 * Contains all blocks within the frame volume
 */
public class FullFrame implements Frame {

    private final Schematic schematic;
    private final int duration;

    public FullFrame(Schematic schematic, int duration) {
        this.schematic = schematic;
        this.duration = duration;
    }

    @Override
    public Instance newInstance() {
        return new OriginInstance();
    }

    private class OriginInstance implements Instance {

        @Override
        public int getDuration() {
            return duration;
        }

        @Override
        public void paste(World world, Vector3i position, BlockChangeFlag flag) {
            Location<World> location = new Location<>(world, position);
            schematic.apply(location, flag, Animator.cause);
        }

        @Override
        public void reset(World world, Vector3i position, BlockChangeFlag flag) {
            Location<World> location = new Location<>(world, position);
            schematic.apply(location, flag, Animator.cause);
        }
    }
}
