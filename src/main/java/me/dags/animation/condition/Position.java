package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class Position implements Condition<Location<World>> {

    private final String id;
    private final String world;
    private final Vector3i min;
    private final Vector3i max;

    public Position(String id, String world, Vector3i min, Vector3i max) {
        this.id = id;
        this.world = world;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getId() {
        return null;
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

        Position position = (Position) o;

        return id != null ? id.equals(position.id) : position.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    private boolean greater(Vector3d pos, Vector3i min) {
        return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ();
    }

    private boolean lesser(Vector3d pos, Vector3i max) {
        return pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
    }
}
