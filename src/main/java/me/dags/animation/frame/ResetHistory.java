package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class ResetHistory implements Frame.History {

    private final List<Vector3i> positions = new LinkedList<>();

    @Override
    public void record(int x, int y, int z, BlockState state) {
        positions.add(new Vector3i(x, y, z));
    }

    @Override
    public void apply(World world, Vector3i position, BlockChangeFlag flag) {
        for (Vector3i pos : positions) {
            world.setBlockType(pos, BlockTypes.AIR, Animator.getCause());
        }
    }
}
