package me.dags.animation.condition;

import com.google.gson.JsonObject;
import me.dags.animation.registry.ConditionRegistry;
import org.spongepowered.api.CatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface Condition<T> extends CatalogType {

    boolean test(T t);

    String getType();

    JsonObject toJson();

    void register(ConditionRegistry registry);

    default JsonObject toTypedJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", getType());
        return object;
    }
}
