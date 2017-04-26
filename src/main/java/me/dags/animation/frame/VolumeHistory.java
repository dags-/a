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
        for (int y = history.getBlockMin().getY(); y <= history.getBlockMax().getY(); y++) {
            for (int z = history.getBlockMin().getZ(); z <= history.getBlockMax().getZ(); z++) {
                for (int x = history.getBlockMin().getX(); x <= history.getBlockMax().getX(); x++) {
                    world.setBlock(x, y, z, history.getBlock(x, y, z), Animator.getCause());
                }
            }
        }
    }
}
