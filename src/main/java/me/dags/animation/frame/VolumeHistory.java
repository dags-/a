package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.BlockVolume;

/**
 * @author dags <dags@dags.me>
 */
public class VolumeHistory implements Frame.History {

    private final BlockVolume history;

    public VolumeHistory(BlockVolume volume) {
        this.history = volume;
    }

    @Override
    public Vector3i getMin() {
        return history.getBlockMin();
    }

    @Override
    public Vector3i getMax() {
        return history.getBlockMax();
    }

    @Override
    public void apply(World world, Vector3i position, BlockChangeFlag flag) {
        // TODO test
        Frame.virtualPaste(world, history, position, flag, true);
    }
}
