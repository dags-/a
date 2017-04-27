package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3i;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class TriggerRecorder {

    private final ItemType wand;
    private String world = "";
    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;

    public TriggerRecorder(ItemType type) {
        this.wand = type;
    }

    public ItemType getWand() {
        return wand;
    }

    public void setPos(Player player, Location<World> location) {
        world = location.getExtent().getName();
        if (pos1 == Vector3i.ZERO) {
            pos1 = location.getBlockPosition();
            FMT.info("Set pos1 ").stress(pos2).tell(player);
        } else if (pos2 == Vector3i.ZERO) {
            pos2 = location.getBlockPosition();
            FMT.info("Set pos2 ").stress(pos2).tell(player);
        } else {
            pos1 = Vector3i.ZERO;
            pos2 = Vector3i.ZERO;
            FMT.info("Reset pos1 & pos2").tell(player);
        }
    }

    public Optional<Position> makePosition(Player player, String name) {
        if (pos1 == Vector3i.ZERO) {
            FMT.error("pos1 has not been set").tell(player);
            return Optional.empty();
        }
        if (pos2 == Vector3i.ZERO) {
            FMT.error("pos2 has not been set").tell(player);
            return Optional.empty();
        }
        Vector3i min = pos1.min(pos2);
        Vector3i max = pos1.max(pos2);
        return Optional.of(new Position(name, world, min, max));
    }

    public Optional<Position.Interact> makeInteract(Player player, String name) {
        if (pos1 == Vector3i.ZERO) {
            FMT.error("pos1 has not been set").tell(player);
            return Optional.empty();
        }
        if (pos2 == Vector3i.ZERO) {
            FMT.error("pos2 has not been set").tell(player);
            return Optional.empty();
        }
        Vector3i min = pos1.min(pos2);
        Vector3i max = pos1.max(pos2);
        return Optional.of(new Position.Interact(name, world, min, max));
    }

    public Optional<Radius> makeRadius(Player player, String name, int radius) {
        if (pos1 == Vector3i.ZERO) {
            FMT.error("pos1 has not been set").tell(player);
            return Optional.empty();
        }
        return Optional.of(new Radius(name, world, pos1, radius));
    }
}
