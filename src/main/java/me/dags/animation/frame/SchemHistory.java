package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class SchemHistory implements Frame.History {

    private final List<Block> history = new LinkedList<>();

    @Override
    public void record(int x, int y, int z, BlockState state) {
        history.add(new Block(x, y , z, state));
    }

    @Override
    public void apply(World world, Vector3i position, BlockChangeFlag flag) {
        for (Block block : history) {
            Frame.setBlock(world, block.x, block.y, block.z, block.state, flag);
        }
    }

    private static class Block {

        private final int x, y, z;
        private final BlockState state;

        private Block(int x, int y, int z, BlockState state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }
}
