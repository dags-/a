package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BlockVolume;

/**
 * @author dags <dags@dags.me>
 */
public interface Frame {

    History apply(World world, Vector3i position, BlockChangeFlag flag);

    Vector3i getMin();

    Vector3i getMax();

    int getDuration();

    DataContainer toContainer();

    interface History {

        void record(int x, int y, int z, BlockState state);

        void apply(World world, Vector3i position, BlockChangeFlag flag);
    }

    static void paste(World world, BlockVolume source, Vector3i pos, BlockChangeFlag flag, boolean withAir, Frame.History history) {
        source.getBlockWorker(Animator.getCause()).iterate((v, x, y, z) -> {
            BlockState state = v.getBlock(x, y, z);

            if (!withAir || state.getType() != BlockTypes.AIR) {
                int posX = pos.getX() + x;
                int posY = pos.getY() + y;
                int posZ = pos.getZ() + z;

                BlockState current = world.getBlock(posX, posY, posZ);
                if (current == state) {
                    current = BlockTypes.AIR.getDefaultState();
                }

                history.record(posX, posY, posZ, current);
                setBlock(world, posX, posY, posZ, state, flag);
            }
        });
    }

    static void setBlock(World world, int x, int y, int z, BlockState state, BlockChangeFlag flag) {
        world.setBlock(x, y, z, state, flag, Animator.getCause());
    }

    static void setVirtualBlock(World world, int x, int y, int z, BlockState state) {
        world.sendBlockChange(x, y, z, state);
    }
}
