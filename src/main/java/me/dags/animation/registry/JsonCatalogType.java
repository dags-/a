package me.dags.animation.registry;

import com.google.gson.JsonObject;
import org.spongepowered.api.CatalogType;

/**
 * @author dags <dags@dags.me>
 */
public interface JsonCatalogType extends CatalogType {

    String getType();

    void populate(JsonObject object);

    default String getFileName() {
        return getId().replace(':', '_') + ".json";
    }

    default JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", getType());
        object.addProperty("name", getName());
        populate(object);
        return object;
    }
}
