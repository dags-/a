package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.PositionRecorder;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class ConditionRecorder extends PositionRecorder {

    public ConditionRecorder(ItemType type) {
        super(type);
    }

    public Optional<Position> makePosition(Player player, String name) {
        Optional<Vector3i> pos1 = getFromEnd(1);
        Optional<Vector3i> pos2 = getFromEnd(0);

        if (!pos1.isPresent() || pos2.isPresent()) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        Vector3i min = pos1.get().min(pos2.get());
        Vector3i max = pos1.get().max(pos2.get());
        return Optional.of(new Position(name, getWorld(), min, max));
    }

    public Optional<Position.Interact> makeInteract(Player player, String name) {
        Optional<Vector3i> pos1 = getFromEnd(1);
        Optional<Vector3i> pos2 = getFromEnd(0);

        if (!pos1.isPresent() || pos2.isPresent()) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        Vector3i min = pos1.get().min(pos2.get());
        Vector3i max = pos1.get().max(pos2.get());
        return Optional.of(new Position.Interact(name, getWorld(), min, max));
    }

    public Optional<Radius> makeRadius(Player player, String name, int radius) {
        Optional<Vector3i> pos1 = getFromEnd(0);

        if (!pos1.isPresent()) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        return Optional.of(new Radius(name, getWorld(), pos1.get(), radius));
    }
}
