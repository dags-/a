package me.dags.animation;

import com.google.inject.Inject;
import me.dags.commandbus.CommandBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "animator", name = "animator", version = "0.1", description = "doot")
public class Animator {

    public static Cause cause;
    public static Animator instance;

    private final Recorder recorder = new Recorder();

    @Inject
    public Animator(PluginContainer container) {
        cause = Cause.source(container).build();
        instance = this;
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create().register(recorder).submit(this);
        Sponge.getEventManager().registerListeners(this, recorder);
    }
}
