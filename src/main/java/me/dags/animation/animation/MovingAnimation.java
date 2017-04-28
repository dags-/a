package me.dags.animation.animation;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dags.animation.Sequence;
import me.dags.animation.frame.Frame;
import me.dags.animation.util.Serializers;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class MovingAnimation implements Animation {

    private final Animation animation;
    private final Sequence<Vector3i> positions;

    private Vector3i offset = new Vector3i(0, 0, 0);

    private MovingAnimation(Animation animation, Sequence<Vector3i> positions) {
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
            offset = offset.add(positions.next());
            Vector3i pos = position.add(offset);
            return animation.play(world, pos);
        }
        return 0;
    }

    @Override
    public void reset() {
        animation.reset();
        positions.goToStart();
    }

    public static Factory.Builder factoryBuilder() {
        return new Factory.Builder();
    }

    public static Factory factoryOf(Collection<Vector3i> path) {
        return factoryBuilder().path(path).build();
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
            return new MovingAnimation(animation, Sequence.of(path));
        }

        @Override
        public String getType() {
            return "moving";
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
