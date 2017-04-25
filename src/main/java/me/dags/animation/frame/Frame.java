package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public interface Frame {

    Instance newInstance();

    interface Instance {

        int getDuration();

        void paste(World world, Vector3i position, BlockChangeFlag flag);

        void reset(World world, Vector3i position, BlockChangeFlag flag);
    }
}
