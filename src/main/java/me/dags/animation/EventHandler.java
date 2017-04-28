package me.dags.animation;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.condition.Condition;
import me.dags.animation.condition.PositionRecorder;
import me.dags.animation.condition.WorldConditions;
import me.dags.animation.frame.FrameRecorder;
import me.dags.animation.handler.AnimationHandler;
import me.dags.animation.registry.ConditionRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class EventHandler {

    @Listener (order = Order.PRE)
    public void interactWand(InteractItemEvent.Secondary.MainHand event, @Root Player player) {
        Optional<Vector3d> position = event.getInteractionPoint();
        if (position.isPresent()) {
            Optional<FrameRecorder> recorder = Animator.getFrameRecorder(player.getUniqueId());
            if (recorder.isPresent()) {
                Optional<ItemType> item = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
                if (item.filter(i -> i == recorder.get().getWand()).isPresent()) {
                    recorder.get().setPos(player, position.get().toInt());
                }
            }

            Optional<PositionRecorder> positionRecorder = Animator.getPositionRecorder(player.getUniqueId());
            if (positionRecorder.isPresent()) {
                Optional<ItemType> item = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
                if (item.filter(i -> i == positionRecorder.get().getWand()).isPresent()) {
                    positionRecorder.get().setPos(player, position.get().toInt());
                }
            }
        }
    }

    @Listener (order = Order.POST)
    public void chatKeyword(MessageChannelEvent.Chat event, @Root Player player) {
        if (event.isCancelled() || event.isMessageCancelled()) {
            return;
        }

        String message = event.getRawMessage().toPlain().trim();
        ConditionRegistry registry = Animator.getConditionRegistry();
        Aggregator aggregator = Animator.getAggregator(player.getUniqueId());
        for (Condition<String> condition : registry.getTextual()) {
            if (condition.test(message)) {
                aggregator.add(condition);
            }
        }
    }

    @Listener (order = Order.POST)
    public void interactBlock(InteractBlockEvent event, @Root Player player) {
        Optional<Location<World>> location = event.getTargetBlock().getLocation();
        if (location.isPresent()) {
            Vector3i position = location.get().getBlockPosition();
            WorldConditions conditions = Animator.getConditionRegistry().getWorldConditions(player.getWorld());
            Aggregator aggregator = Animator.getAggregator(player.getUniqueId());
            for (Condition<Vector3i> condition : conditions.getInteractable()) {
                if (condition.test(position)) {
                    aggregator.add(condition);
                }
            }
        }
    }

    @Listener (order = Order.POST)
    public void disconnect(ClientConnectionEvent.Disconnect event) {
        Animator.dropAggregator(event.getTargetEntity().getUniqueId());
    }

    public void process() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            World world = player.getWorld();

            ConditionRegistry registry = Animator.getConditionRegistry();
            WorldConditions conditions = registry.getWorldConditions(world);
            Aggregator aggregator = Animator.getAggregator(player.getUniqueId());

            // test position
            Vector3i position = player.getLocation().getBlockPosition();
            for (Condition<Vector3i> condition : conditions.getPositional()) {
                if (condition.test(position)) {
                    aggregator.add(condition);
                }
            }

            // test permissions
            for (Condition<Subject> condition : registry.getPermission()) {
                if (condition.test(player)) {
                    aggregator.add(condition);
                }
            }

            // handle conditions
            for (AnimationHandler handler : Animator.getHandlers(world).getAll()) {
                if (!handler.isActive()) {
                    handler.process(world, aggregator);
                }
            }

            // reset aggregator
            aggregator.reset();
        }
    }
}
