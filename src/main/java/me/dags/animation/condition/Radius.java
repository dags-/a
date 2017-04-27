package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonObject;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.util.Serializers;

/**
 * @author dags <dags@dags.me>
 */
public class Radius implements Condition<Vector3i> {

    private final String id;
    private final String name;
    private final String world;
    private final Vector3i position;
    private final int radius;
    private final int radiusSq;

    public Radius(String name, String world, Vector3i position, int radius) {
        this.id = getType() + ":" + name;
        this.name = name;
        this.world = world;
        this.position = position;
        this.radius = radius;
        this.radiusSq = radius * radius;
    }

    @Override
    public String getType() {
        return "radius";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean test(Vector3i position) {
        return this.position.distanceSquared(position) <= radiusSq;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = toTypedJson();
        object.addProperty("world", world);
        object.addProperty("radius", radius);
        object.add("position", Serializers.vector(position));
        return object;
    }

    @Override
    public void register(ConditionRegistry registry) {
        if (registry.register(this)) {
            registry.getWorldConditions(world).registerPositional(this);
        }
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
