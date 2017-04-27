package me.dags.animation;

import com.flowpowered.math.vector.Vector3d;
import me.dags.animation.animation.AnimationHandler;
import me.dags.animation.condition.Aggregator;
import me.dags.animation.frame.Recorder;
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
            Optional<Recorder> recorder = Animator.getRecorder(player.getUniqueId());
            if (recorder.isPresent()) {
                Optional<ItemType> item = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
                if (item.filter(i -> i == recorder.get().getWand()).isPresent()) {
                    recorder.get().setPos(player, position.get().toInt());
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
        ConditionRegistry registry = Animator.getConditions(player.getWorld());
        Aggregator aggregator = Animator.getAggregator(player.getUniqueId());
        registry.getTextual().stream().filter(condition -> condition.test(message)).forEach(aggregator::add);
    }

    @Listener (order = Order.POST)
    public void interactBlock(InteractBlockEvent event, @Root Player player) {
        Optional<Location<World>> location = event.getTargetBlock().getLocation();
        if (location.isPresent()) {
            Location<World> loc = location.get();
            ConditionRegistry registry = Animator.getConditions(player.getWorld());
            Aggregator aggregator = Animator.getAggregator(player.getUniqueId());
            registry.getInteractable().stream().filter(condition -> condition.test(loc)).forEach(aggregator::add);
        }
    }

    @Listener (order = Order.POST)
    public void disconnect(ClientConnectionEvent.Disconnect event) {
        Animator.dropAggregator(event.getTargetEntity().getUniqueId());
    }

    public void process() {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            World world = player.getWorld();

            // update player position
            Aggregator aggregator = Animator.getAggregator(player.getUniqueId());
            Location<World> location = player.getLocation();
            Animator.getConditions(world).getPositional().stream()
                    .filter(condition -> condition.test(location)).forEach(aggregator::add);

            // handle conditions
            for (AnimationHandler handler : Animator.getHandlers(world)) {
                if (!handler.isActive()) {
                    handler.process(world, aggregator);
                }
            }

            // reset aggregator
            aggregator.reset();
        }
    }
}
