package me.dags.animation.util;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.condition.Condition;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class Serializers {

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
