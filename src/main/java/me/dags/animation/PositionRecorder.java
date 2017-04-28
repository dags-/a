package me.dags.animation;

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

    public String getWorld() {
        return world;
    }

    public List<Vector3i> getPositions() {
        return positions;
    }

    public Optional<Vector3i> get(int index) {
        return getSize() > index ? Optional.of(positions.get(index)) : Optional.empty();
    }

    public Optional<Vector3i> getFromEnd(int count) {
        if (getSize() > count) {
            return Optional.of(positions.get(getSize() - (count + 1)));
        }
        return Optional.empty();
    }

    public Optional<Vector3i> getFirst() {
        return get(0);
    }

    public Optional<Vector3i> getLast() {
        return getFromEnd(0);
    }

    public int getSize() {
        return positions.size();
    }

    public void reset() {
        positions.clear();
    }

    public void setPos(Player player, Vector3i position) {
        world = player.getWorld().getName();
        positions.add(position);
        FMT.info("Set ").stress("pos%s", getSize() - 1).info(" to ").stress(position).tell(player);
    }

    public Optional<MovingAnimation.Factory> calculatePath(Player player, String name, int steps) {
        if (getSize() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        MovingAnimation.Factory.Builder builder = MovingAnimation.factoryBuilder().name(name);

        List<Vector3i> positions = getPositions();
        Vector3i from = positions.get(0);
        for (int i = 1; i < getSize(); i++) {
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
        if (getSize() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }
        MovingAnimation.Factory factory = MovingAnimation.factoryBuilder().name(name).path(getPositions()).build();
        return Optional.of(factory);
    }
}
