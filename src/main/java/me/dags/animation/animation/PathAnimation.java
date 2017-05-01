package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import me.dags.animation.util.Serializers;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author dags <dags@dags.me>
 */
public class PathAnimation implements Animation {

    private static final Predicate<Entity> LIVING = Living.class::isInstance;

    private final Animation animation;
    private final Sequence<Vector3i> positions;

    private Vector3i offset = Vector3i.ZERO;
    private int direction = 1;

    private PathAnimation(Animation animation, Sequence<Vector3i> positions) {
        this.animation = animation;
        this.positions = positions;
    }

    @Override
    public Sequence<Frame> getTimeline() {
        return animation.getTimeline();
    }

    @Override
    public boolean hasFinished() {
        return !positions.hasNext() || animation.hasFinished();
    }

    @Override
    public int play(World world, Vector3i position) {
        if (positions.hasNext()) {
            Vector3i increment = positions.next().mul(direction);

            Frame frame = animation.getTimeline().current();
            if (frame != null) {
                Vector3i lastPos = position.add(offset);
                moveLiving(world, lastPos.add(frame.getMin()), lastPos.add(frame.getMax()), increment);
            }

            offset = offset.add(increment);

            return animation.play(world, position.add(offset));
        }
        return 0;
    }

    @Override
    public void reset() {
        animation.reset();
        positions.setDirection(1).reset();
        direction = 1;
    }

    @Override
    public void reverse() {
        animation.reverse();
        positions.skip().reverse();
        direction = -direction;
    }

    private void moveLiving(World world, Vector3i min, Vector3i max, Vector3i offset) {
        Collection<Entity> entities = world.getExtentView(min, max).getEntities(LIVING);
        for (Entity entity : entities) {
            entity.setLocation(entity.getLocation().add(offset));
        }
    }

    public static Factory.Builder factoryBuilder() {
        return new Factory.Builder();
    }

    public static class Factory implements AnimationFactory {

        private final List<Vector3i> path;
        private final String id;
        private final String name;

        private Factory(Builder builder) {
            this.name = builder.name;
            this.id = getType() + ":" + getName();
            this.path = ImmutableList.copyOf(builder.path);
        }

        @Override
        public Animation create(Animation animation) {
            return new PathAnimation(animation, Sequence.of(path));
        }

        @Override
        public String getType() {
            return "path";
        }

        @Override
        public void populate(JsonObject object) {
            JsonArray array = new JsonArray();
            for (Vector3i pos : path) {
                array.add(Serializers.vector(pos));
            }
            object.add("path", array);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        public static class Builder {

            private List<Vector3i> path = new LinkedList<>();
            private String name = "";

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder path(Collection<Vector3i> path) {
                this.path.addAll(path);
                return this;
            }

            public Builder pos(Vector3i vector3i) {
                path.add(vector3i);
                return this;
            }

            public Factory build() {
                return new Factory(this);
            }
        }
    }
}
