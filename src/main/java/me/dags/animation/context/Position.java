package me.dags.animation.context;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class Position implements Condition<Location<World>> {

    private final String world;
    private final Vector3i min;
    private final Vector3i max;

    public Position(String world, Vector3i min, Vector3i max) {
        this.world = world;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean test(Location<World> location) {
        if (location.getExtent().getName().equals(world)) {
            Vector3d pos = location.getPosition();
            return greater(pos, min) && lesser(pos, max);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position locatable = (Position) o;

        if (world != null ? !world.equals(locatable.world) : locatable.world != null) return false;
        if (min != null ? !min.equals(locatable.min) : locatable.min != null) return false;
        return max != null ? max.equals(locatable.max) : locatable.max == null;
    }

    @Override
    public int hashCode() {
        int result = world != null ? world.hashCode() : 0;
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        return result;
    }

    private boolean greater(Vector3d pos, Vector3i min) {
        return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ();
    }

    private boolean lesser(Vector3d pos, Vector3i max) {
        return pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
    }
}
