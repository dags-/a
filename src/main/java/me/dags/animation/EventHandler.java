package me.dags.animation;

import com.flowpowered.math.vector.Vector3d;
import me.dags.animation.frame.Recorder;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class EventHandler {

    @Listener (order = Order.PRE)
    public void interact(InteractItemEvent.Secondary.MainHand event, @Root Player player) {
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
}
