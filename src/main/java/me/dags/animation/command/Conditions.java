package me.dags.animation.command;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.Animator;
import me.dags.animation.Permissions;
import me.dags.animation.PositionRecorder;
import me.dags.animation.condition.*;
import me.dags.animation.util.Utils;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.annotation.Permission;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.nio.file.Path;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class Conditions {

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "record", parent = "condition")
    public void wand(@Caller Player player) {
        Optional<ItemType> inHand = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
        if (!inHand.isPresent()) {
            FMT.error("You must be holding an item to use as your selection wand").tell(player);
            return;
        }

        FMT.info("Setting your selection wand to item: ").stress(inHand.get().getName()).tell(player);
        Animator.createRecorder(player.getUniqueId(), inHand.get());
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "position", parent = "condition")
    public void position(@Caller Player player, @One("name") String name) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }

        Optional<Position> position = recorder.get().makePosition(player, name);
        if (position.isPresent()) {
            WorldConditions conditions = Animator.getConditionRegistry().getWorldConditions(player.getWorld());
            if (conditions.hasRegistered(position.get().getId())) {
                FMT.error("A condition with that name already exists").tell(player);
                return;
            }

            FMT.info("Registering condition ").stress(position.get()).tell(player);
            position.get().register(Animator.getConditionRegistry());

            Path out = conditions.getConditionsDir().resolve(position.get().getFileName());
            Utils.write(position.get(), out);
        }
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "interact", parent = "condition")
    public void interact(@Caller Player player, @One("name") String name) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }

        Optional<Position.Interact> interact = recorder.get().makeInteract(player, name);
        if (interact.isPresent()) {
            WorldConditions conditions = Animator.getConditionRegistry().getWorldConditions(player.getWorld());
            if (conditions.hasRegistered(interact.get().getId())) {
                FMT.error("A condition with that name already exists").tell(player);
                return;
            }

            FMT.info("Registering condition ").stress(interact.get()).tell(player);
            interact.get().register(Animator.getConditionRegistry());
            Path out = conditions.getConditionsDir().resolve(interact.get().getFileName());
            Utils.write(interact.get(), out);
        }
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "radius", parent = "condition")
    public void radius(@Caller Player player, @One("name") String name, @One("radius") int rad) {
        Optional<PositionRecorder> recorder = Animator.getPositionRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording any positions").tell(player);
            return;
        }

        Optional<Radius> radius = recorder.get().makeRadius(player, name, rad);
        if (radius.isPresent()) {
            WorldConditions conditions = Animator.getConditionRegistry().getWorldConditions(player.getWorld());
            if (conditions.hasRegistered(radius.get().getId())) {
                FMT.error("A condition with that name already exists").tell(player);
                return;
            }

            FMT.info("Registering condition ").stress(radius.get()).tell(player);
            radius.get().register(Animator.getConditionRegistry());
            Path out = conditions.getConditionsDir().resolve(radius.get().getFileName());
            Utils.write(radius.get(), out);
        }
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "keyword", parent = "condition")
    public void keyword(@Caller CommandSource source, String name, String word) {
        Keyword keyword = new Keyword(name, word);
        if (Animator.getConditionRegistry().hasRegisteredTextual(keyword.getId())) {
            FMT.error("A text condition by that name already exists").tell(source);
            return;
        }

        FMT.info("Registering condition ").stress(keyword).tell(source);
        keyword.register(Animator.getConditionRegistry());

        Path out = Animator.getConditionRegistry().getConditionsDir().resolve(keyword.getFileName());
        Utils.write(keyword, out);
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "permission", parent = "condition")
    public void permission(@Caller CommandSource source, @One("id") String perm) {
        Perm permission = new Perm(perm);
        if (Animator.getConditionRegistry().hasRegisteredTextual(permission.getId())) {
            FMT.error("A text condition by that name already exists").tell(source);
            return;
        }

        FMT.info("Registering condition ").stress(permission).tell(source);
        permission.register(Animator.getConditionRegistry());

        Path out = Animator.getConditionRegistry().getConditionsDir().resolve(permission.getFileName());
        Utils.write(permission, out);
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "position", parent = "condition test")
    public void testLocation(@Caller Player player, @One("condition") Condition condition) {
        WorldConditions conditions = Animator.getConditionRegistry().getWorldConditions(player.getWorld());
        if (!conditions.hasRegistered(condition.getId())) {
            FMT.error("Condition not valid position for the current world ").stress(condition).tell(player);
            return;
        }

        Vector3i position = player.getLocation().getBlockPosition();
        @SuppressWarnings("unchecked")
        boolean result = condition.test(position);
        FMT.info("pos: ").stress(position).info(", condition: ").stress(condition).info(", result: ").stress(result).tell(player);
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "keyword", parent = "condition test")
    public void testKeyword(@Caller CommandSource source, @One("condition") Condition condition, @One("word") String word) {
        @SuppressWarnings("unchecked")
        boolean result = condition.test(word);
        FMT.info("word: ").stress(word).info(", condition: ").stress(condition).info(", result: ").stress(result).tell(source);
    }

    @Permission(Permissions.CONDITION_COMMAND)
    @Command(alias = "permission", parent = "condition test")
    public void testPermission(@Caller CommandSource source, @One("condition") Condition condition) {
        @SuppressWarnings("unchecked")
        boolean result = condition.test(source);
        FMT.info("subject: ").stress(source.getName()).info(", condition: ").stress(condition).info(", result: ").stress(result).tell(source);
    }
}
