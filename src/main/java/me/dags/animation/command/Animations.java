package me.dags.animation.command;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
import me.dags.animation.Permissions;
import me.dags.animation.PositionRecorder;
import me.dags.animation.animation.PathAnimation;
import me.dags.animation.util.Utils;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.annotation.Permission;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class Animations {

    @Permission(Permissions.ANIMATION_COMMAND)
    @Command(alias = "record", parent = "path")
    public void record(@Caller Player player) {
        Optional<ItemType> inHand = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
        if (inHand.isPresent()) {
            FMT.info("Position recorder wand bound to ").stress(inHand.get().getName()).tell(player);
            Animator.createPositionRecorder(player.getUniqueId(), inHand.get());
        } else {
            FMT.error("You must be holding an item to use as your wand").tell(player);
        }
    }

    @Permission(Permissions.ANIMATION_COMMAND)
    @Command(alias = "create", parent = "path")
    public void setPath(@Caller Player player, String name) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }
        recorder.get().getPath(player).ifPresent(list -> path(player, name, list));
    }

    @Permission(Permissions.ANIMATION_COMMAND)
    @Command(alias = "calc", parent = "path")
    public void calcPath(@Caller Player player, @One("name") String name) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }
        recorder.get().calculatePath(player).ifPresent(list -> path(player, name, list));
    }

    private void path(Player player, String name, List<Vector3i> path) {
        if (path.size() < 2) {
            FMT.error("The path has less than 2 positions!").tell(player);
            return;
        }

        PathAnimation.Factory factory = PathAnimation.factoryBuilder().name(name).path(path).build();
        if (Animator.getAnimationRegistry().hasRegistered(factory.getId())) {
            FMT.error("An animation by that name already exists").tell(player);
            return;
        }

        FMT.info("Registering animation ").stress(factory.getId()).tell(player);
        Animator.getAnimationRegistry().register(factory);
        Utils.write(factory, Animator.getAnimationRegistry().getAnimationsDir().resolve(factory.getFileName()));
    }
}
