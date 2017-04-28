package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonObject;
import me.dags.animation.registry.ConditionRegistry;
import me.dags.animation.util.Serializers;

/**
 * @author dags <dags@dags.me>
 */
public class Position implements Condition<Vector3i> {

    private final String id;
    private final String name;
    private final String world;
    private final Vector3i min;
    private final Vector3i max;

    public Position(String name, String world, Vector3i min, Vector3i max) {
        this.id = getType() + ":" + name;
        this.name = name;
        this.world = world;
        this.min = min;
        this.max = max;
    }

    @Override
    public String getType() {
        return "position";
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
    public void populate(JsonObject object) {
        object.addProperty("world", world);
        object.add("min", Serializers.vector(min));
        object.add("max", Serializers.vector(max));
    }

    @Override
    public boolean test(Vector3i position) {
        return greater(position, min) && lesser(position, max);
    }

    @Override
    public void register(ConditionRegistry registry) {
        registry.registerGlobal(this);
        registry.getWorldConditions(world).registerPositional(this);
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

    @Override
    public String toString() {
        return String.format("name=%s, world=%s, min=%s, max=%s", name, world, min, max);
    }

    private boolean greater(Vector3i pos, Vector3i min) {
        return pos.getX() >= min.getX() && pos.getY() >= min.getY() && pos.getZ() >= min.getZ();
    }

    private boolean lesser(Vector3i pos, Vector3i max) {
        return pos.getX() <= max.getX() && pos.getY() <= max.getY() && pos.getZ() <= max.getZ();
    }

    public static class Interact extends Position {

        public Interact(String name, String world, Vector3i min, Vector3i max) {
            super(name, world, min, max);
        }

        @Override
        public String getType() {
            return "interact";
        }

        @Override
        public void register(ConditionRegistry registry) {
            registry.registerGlobal(this);
            registry.getWorldConditions(super.world).registerInteractable(this);
        }
    }
}
