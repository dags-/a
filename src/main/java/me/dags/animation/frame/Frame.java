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

    int getDuration();

    DataContainer toContainer();

    interface History {

        Vector3i getMin();

        Vector3i getMax();

        void apply(World world, Vector3i position, BlockChangeFlag flag);
    }

    static void paste(World world, BlockVolume source, Vector3i pos, BlockChangeFlag flag, boolean withAir) {
        source.getBlockWorker(Animator.getCause()).iterate((v, x, y, z) -> {
            BlockState state = v.getBlock(x, y, z);
            if (withAir || state.getType() != BlockTypes.AIR) {
                world.setBlock(x + pos.getX(), y + pos.getY(), z + pos.getZ(), state, flag, Animator.getCause());
            }
        });
    }

    static void virtualPaste(World world, BlockVolume source, Vector3i pos, BlockChangeFlag flag, boolean withAir) {
        source.getBlockWorker(Animator.getCause()).iterate((v, x, y, z) -> {
            BlockState state = v.getBlock(x, y, z);
            if (withAir || state.getType() != BlockTypes.AIR) {
                world.sendBlockChange(x + pos.getX(), y + pos.getY(), z + pos.getZ(), state);
            }
        });
    }
}
