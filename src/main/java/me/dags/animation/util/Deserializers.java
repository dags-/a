package me.dags.animation.util;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dags.animation.Animator;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.condition.*;
import me.dags.animation.frame.FrameList;
import me.dags.animation.handler.AnimationHandler;
import me.dags.animation.handler.WorldHandlers;
import me.dags.animation.registry.AnimationRegistry;
import me.dags.animation.registry.ConditionRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * @author dags <dags@dags.me>
 */
public class Deserializers {

    private static final Map<String, Function<JsonElement, Condition<?>>> conditionMappers = ImmutableMap.<String, Function<JsonElement, Condition<?>>>builder()
            .put("permission", Deserializers::permission)
            .put("position", Deserializers::position)
            .put("interact", Deserializers::interact)
            .put("keyword", Deserializers::keyword)
            .put("radius", Deserializers::radius)
            .build();

    public static void loadAnimations(AnimationRegistry registry, Path dir) {
//        Utils.readDir(dir)
//                .filter(Utils::isJsonFile)
//                .map(Utils::read)
//                .filter(JsonElement::isJsonObject)
//                .map(Deserializers::condition)
//                .filter(Objects::nonNull)
//                .forEach(animation -> );
    }

    public static void loadConditions(ConditionRegistry registry, Path dir) {
        Utils.readDir(dir)
                .filter(Utils::isJsonFile)
                .map(Utils::read)
                .filter(JsonElement::isJsonObject)
                .map(Deserializers::condition)
                .filter(Objects::nonNull)
                .forEach(condition -> condition.register(registry));
    }

    public static void loadHandlers(WorldHandlers registry, Path dir) {
        Utils.readDir(dir)
                .filter(Utils::isJsonFile)
                .map(Utils::read)
                .filter(JsonElement::isJsonObject)
                .map(Deserializers::handler)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(registry::register);
    }

    public static Optional<AnimationHandler> handler(JsonElement element) {
        JsonObject root = element.getAsJsonObject();
        AnimationHandler.Builder builder = AnimationHandler.builder();
        builder.name(Utils.get(root, "name", JsonElement::getAsString, null));
        builder.world(Utils.get(root, "world", JsonElement::getAsString, null));
        builder.origin(Utils.get(root, "origin", Deserializers::vector3i, null));
        builder.sequenceProvider(Utils.get(root, "sequenceProvider", Deserializers::frames, null));
        builder.triggers(Utils.get(root, "triggers", Deserializers::triggers, Collections.emptyList()));
        builder.animations(Utils.get(root, "animations", Deserializers::factories, Collections.emptyList()));
        return Optional.of(builder.build());
    }

    public static Vector3i vector3i(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int z = obj.get("z").getAsInt();
            return new Vector3i(x, y, z);
        }
        return Vector3i.ZERO;
    }

    private static FrameList frames(JsonElement element) {
        return new FrameList(element.getAsString());
    }

    private static List<AnimationFactory> factories(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            List<AnimationFactory> factories = new LinkedList<>();
            for (JsonElement e : arr) {
                Optional<AnimationFactory> factory = Sponge.getRegistry().getType(AnimationFactory.class, e.getAsString());
                if (factory.isPresent()) {
                    factories.add(factory.get());
                }
            }
            return factories;
        }
        return Collections.emptyList();
    }

    private static List<List<Condition<?>>> triggers(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            List<List<Condition<?>>> triggers = new LinkedList<>();
            for (JsonElement e : arr) {
                List<Condition<?>> conditions = conditions(e);
                if (!conditions.isEmpty()) {
                    triggers.add(conditions);
                }
            }
            return triggers;
        }
        return Collections.emptyList();
    }

    private static List<Condition<?>> conditions(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            List<Condition<?>> conditions = new LinkedList<>();
            for (JsonElement e : arr) {
                Optional<Condition<?>> condition = Animator.getConditionRegistry().getCondition(e.getAsString());
                if (condition.isPresent()) {
                    conditions.add(condition.get());
                }
            }
            return conditions;
        }
        return Collections.emptyList();
    }

    public static Condition<?> condition(JsonElement e) {
        if (e.isJsonObject() && e.getAsJsonObject().has("type")) {
            String type = e.getAsJsonObject().get("type").getAsString();
            Function<JsonElement, Condition<?>> mapper = conditionMappers.get(type);
            if (mapper != null) {
                return mapper.apply(e);
            }
        }
        return null;
    }

    public static Condition<Subject> permission(JsonElement e) {
        if (isType(e, "permission")) {
            JsonObject object = e.getAsJsonObject();
            String name = object.get("name").getAsString();
            return new Perm(name);
        }
        return null;
    }

    public static Condition<Vector3i> position(JsonElement e) {
        if (isType(e, "position")) {
            JsonObject object = e.getAsJsonObject();
            String name = object.get("name").getAsString();
            String world = object.get("world").getAsString();
            Vector3i min = vector3i(object.get("min"));
            Vector3i max = vector3i(object.get("max"));
            return new Position(name, world, min, max);
        }
        return null;
    }

    public static Condition<Vector3i> interact(JsonElement e) {
        if (isType(e, "interact")) {
            JsonObject object = e.getAsJsonObject();
            String name = object.get("name").getAsString();
            String world = object.get("world").getAsString();
            Vector3i min = vector3i(object.get("min"));
            Vector3i max = vector3i(object.get("max"));
            return new Position.Interact(name, world, min, max);
        }
        return null;
    }

    public static Condition<Vector3i> radius(JsonElement e) {
        if (isType(e, "radius")) {
            JsonObject object = e.getAsJsonObject();
            String name = object.get("name").getAsString();
            String world = object.get("world").getAsString();
            int radius = object.get("radius").getAsInt();
            Vector3i position = vector3i(object.get("position"));
            return new Radius(name, world, position, radius);
        }
        return null;
    }

    public static Condition<String> keyword(JsonElement e) {
        if (isType(e, "keyword")) {
            JsonObject obj = e.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String word = obj.get("keyword").getAsString();
            return new Keyword(name, word);
        }
        return null;
    }

    private static boolean isType(JsonElement e, String type) {
        return e.isJsonObject() && e.getAsJsonObject().has("type") && e.getAsJsonObject().get("type").getAsString().equals(type);
    }
}
