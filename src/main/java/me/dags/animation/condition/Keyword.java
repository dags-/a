package me.dags.animation.condition;

import com.google.gson.JsonObject;
import me.dags.animation.registry.ConditionRegistry;

/**
 * @author dags <dags@dags.me>
 */
public class Keyword implements Condition<String> {

    private final String id;
    private final String name;
    private final String keyword;

    public Keyword(String name, String keyword) {
        this.id = getType() + ":" + name;
        this.name = name;
        this.keyword = keyword;
    }

    @Override
    public String getType() {
        return "keyword";
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
    public boolean test(String s) {
        return s.equalsIgnoreCase(keyword);
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = toTypedJson();
        object.addProperty("keyword", keyword);
        return object;
    }

    @Override
    public void register(ConditionRegistry registry) {
        registry.registerTextual(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword keyword = (Keyword) o;

        return id != null ? id.equals(keyword.id) : keyword.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
