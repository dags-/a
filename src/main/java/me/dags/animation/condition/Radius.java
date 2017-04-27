package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class Radius implements Condition<Location<World>> {

    private final String id;
    private final String world;
    private final Vector3i position;
    private final int radiusSq;

    public Radius(String id, String world, Vector3i position, int radius) {
        this.id = id;
        this.world = world;
        this.position = position;
        this.radiusSq = radius * radius;
    }

    @Override
    public boolean test(Location<World> location) {
        if (location.getExtent().getName().equals(world)) {
            return location.getBlockPosition().distanceSquared(position) <= radiusSq;
        }
        return false;
    }

    @Override
    public String getId() {
        return id;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Radius radius = (Radius) o;

        return id != null ? id.equals(radius.id) : radius.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
