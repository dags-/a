package me.dags.animation.condition;

import com.google.gson.JsonObject;
import me.dags.animation.Permissions;
import me.dags.animation.registry.ConditionRegistry;
import org.spongepowered.api.service.permission.Subject;

/**
 * @author dags <dags@dags.me>
 */
public class Perm implements Condition<Subject> {

    private final String permission;
    private final String name;
    private final String id;

    public Perm(String name) {
        this.name = name.toLowerCase();
        this.permission = Permissions.USE_NODE + getName();
        this.id = getType() + ":" + getName();
    }

    @Override
    public boolean test(Subject subject) {
        return subject.hasPermission(permission);
    }

    @Override
    public String getType() {
        return "permission";
    }

    @Override
    public void populate(JsonObject object) {

    }

    @Override
    public void register(ConditionRegistry registry) {
        registry.registerGlobal(this);
        registry.registerPermission(this);
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
    public String toString() {
        return String.format("name=%s, permission=%s", name, permission);
    }
}
