package me.dags.animation.command;

import me.dags.animation.Animator;
import me.dags.animation.Permissions;
import me.dags.animation.animation.MovingAnimation;
import me.dags.animation.condition.PositionRecorder;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.annotation.Permission;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author dags <dags@dags.me>
 */
public class Animations {

    @Permission(Permissions.ANIMATION_COMMAND)
    @Command(alias = "moving", parent = "animation")
    public void moving(@Caller Player player, String name) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }
        moving(player, () -> recorder.get().makePath(player, name));
    }

    @Permission(Permissions.ANIMATION_COMMAND)
    @Command(alias = "moving", parent = "animation")
    public void moving(@Caller Player player, @One("name") String name, @One("steps") int steps) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }
        moving(player, () -> recorder.get().calculatePath(player, name, steps));
    }

    private void moving(Player player, Supplier<Optional<MovingAnimation.Factory>> supplier) {
        Optional<MovingAnimation.Factory> factory = supplier.get();
        if (factory.isPresent()) {
            if (Animator.getAnimationRegistry().hasRegistered(factory.get().getId())) {
                FMT.error("An animation by that name already exists").tell(player);
                return;
            }

            FMT.info("Registering animation ").stress(factory.get().getId()).tell(player);
            Animator.getAnimationRegistry().register(factory.get());
        }
    }
}
