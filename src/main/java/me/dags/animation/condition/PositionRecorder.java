package me.dags.animation.condition;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.animation.MovingAnimation;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class PositionRecorder {

    private final ItemType wand;
    private String world = "";
    private List<Vector3i> positions = new ArrayList<>();

    public PositionRecorder(ItemType type) {
        this.wand = type;
    }

    public ItemType getWand() {
        return wand;
    }

    public void setPos(Player player, Vector3i position) {
        world = player.getWorld().getName();
        positions.add(position);
        FMT.info("Set pos ").stress(position).tell(player);
    }

    public Optional<Position> makePosition(Player player, String name) {
        if (positions.size() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }
        Vector3i min = positions.get(positions.size() - 2);
        Vector3i max = positions.get(positions.size() - 1);
        return Optional.of(new Position(name, world, min, max));
    }

    public Optional<Position.Interact> makeInteract(Player player, String name) {
        if (positions.size() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }
        Vector3i min = positions.get(positions.size() - 2);
        Vector3i max = positions.get(positions.size() - 1);
        return Optional.of(new Position.Interact(name, world, min, max));
    }

    public Optional<Radius> makeRadius(Player player, String name, int radius) {
        if (positions.size() < 1) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }
        Vector3i position = positions.get(positions.size() - 1);
        return Optional.of(new Radius(name, world, position, radius));
    }

    public Optional<MovingAnimation.Factory> calculatePath(Player player, String name, int steps) {
        if (positions.size() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        MovingAnimation.Factory.Builder builder = MovingAnimation.factoryBuilder().name(name);

        Vector3i from = positions.get(0);
        for (int i = 1; i < positions.size(); i++) {
            positions.add(from);

            Vector3i to = positions.get(i);
            Vector3d vec = to.sub(from).toDouble().div(steps);
            Vector3i last = from;

            for (int j = 0; j < steps - 1; j++) {
                Vector3i pos = from.toDouble().add(vec.mul(i)).toInt();
                if (!pos.equals(last)) {
                    builder.pos(pos.toInt());
                }
                last = pos;
            }

            from = to;
        }

        return Optional.of(builder.build());
    }

    public Optional<MovingAnimation.Factory> makePath(Player player, String name) {
        if (positions.size() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }
        MovingAnimation.Factory factory = MovingAnimation.factoryBuilder().name(name).path(positions).build();
        return Optional.of(factory);
    }
}
