package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public interface Frame {

    History apply(World world, Vector3i position, BlockChangeFlag flag);

    int getDuration();

    DataContainer toContainer();

    interface History {

        void apply(World world, Vector3i position, BlockChangeFlag flag);
    }
}
