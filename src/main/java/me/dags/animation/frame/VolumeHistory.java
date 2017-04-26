package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
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
    public void apply(World world, Vector3i position, BlockChangeFlag flag) {
        world.getBlockWorker(Animator.getCause()).map((volume, x, y, z) -> history.getBlock(x, y, z));
    }
}
