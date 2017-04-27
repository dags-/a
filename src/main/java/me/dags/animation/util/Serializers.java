package me.dags.animation.util;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.AnimationHandler;
import me.dags.animation.condition.Condition;

import java.nio.file.Path;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class Serializers {

    public static void write(AnimationHandler handler, Path parent) {
        JsonObject root = new JsonObject();

        root.addProperty("name", handler.getName());
        root.addProperty("world", handler.getWorld());
        root.addProperty("frames", handler.getFrames().getId());
        root.add("origin", vector(handler.getOrigin()));
        root.add("animations", animations(handler.getFactories()));
        root.add("triggers", triggers(handler.getTriggers()));

        Path output = parent.resolve(handler.getWorld()).resolve(handler.getName() + ".json");
        Utils.write(root, output);
    }

    public static JsonElement vector(Vector3i pos) {
        JsonObject json = new JsonObject();
        json.addProperty("x", pos.getX());
        json.addProperty("y", pos.getY());
        json.addProperty("z", pos.getZ());
        return json;
    }

    public static JsonElement triggers(List<List<Condition<?>>> triggers) {
        JsonArray root = new JsonArray();
        for (List<Condition<?>> conditions : triggers) {
            JsonArray array = new JsonArray();
            for (Condition<?> condition : conditions) {
                array.add(condition.toJson());
            }
            root.add(array);
        }
        return root;
    }

    public static JsonElement animations(List<AnimationFactory> factories) {
        JsonArray array = new JsonArray();
        for (AnimationFactory factory : factories) {
            array.add(new JsonPrimitive(factory.getId()));
        }
        return array;
    }
}
