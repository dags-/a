package me.dags.animation;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;

import java.util.ArrayList;
import java.util.LinkedList;
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
        setPos(player, position, true);
    }

    public void setPos(Player player, Vector3i position, boolean withMessage) {
        world = player.getWorld().getName();
        positions.add(position);
        if (withMessage) {
            FMT.info("Set ").stress("pos%s", getSize() - 1).info(" to ").stress(position).tell(player);
        }
    }

    public Optional<List<Vector3i>> calculatePath(Player player) {
        if (getSize() < 2) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        List<Vector3i> positions = getPositions();
        List<Vector3i> path = new LinkedList<>();
        Vector3i from = positions.get(0);

        for (int i = 1; i < getSize(); i++) {
            Vector3i to = positions.get(i);
            float distance = from.distance(to);

            Vector3d unit = to.sub(from).toDouble().div(distance);
            Vector3d last = Vector3d.ZERO;

            for (int j = 0; j < distance; j++) {
                Vector3d next = unit.mul(j).floor();
                Vector3i pos = next.sub(last).toInt();
                from = from.add(pos);
                if (!pos.equals(Vector3i.ZERO)) {
                    path.add(pos);
                }
                last = next;
            }

            if (!from.equals(to)) {
                path.add(to.sub(from));
            }

            from = to;
        }

        return Optional.of(path);
    }

    public Optional<List<Vector3i>> getPath(Player player) {
        if (getSize() < 1) {
            FMT.error("Not enough positions set").tell(player);
            return Optional.empty();
        }

        List<Vector3i> path = new ArrayList<>();
        Vector3i from = positions.get(0);

        for (int i = 0; i < getSize(); i++) {
            Vector3i to = positions.get(i);
            path.add(to.sub(from));
            from = to;
        }

        return Optional.of(path);
    }
}
