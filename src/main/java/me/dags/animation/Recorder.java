package me.dags.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.frame.Frame;
import me.dags.animation.frame.SchemFrame;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class Recorder {

    private final List<Frame> frames = new ArrayList<>();
    private final ItemType wand;

    private AnimationHandler tester = AnimationHandler.builder().build();
    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;
    private Vector3i origin = Vector3i.ZERO;

    public Recorder(ItemType wand) {
        this.wand = wand;
    }

    public ItemType getWand() {
        return wand;
    }

    public Vector3i getOrigin() {
        return pos1;
    }

    public AnimationHandler getTester() {
        return tester;
    }

    public void setTester(AnimationHandler handler) {
        tester = handler;
    }

    public Optional<Frame> getFrame(int number) {
        if (number > -1 && number < frames.size()) {
            return Optional.ofNullable(frames.get(number));
        }
        return Optional.empty();
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setPos(Player source, Vector3i pos) {
        if (pos1 == Vector3i.ZERO) {
            pos1 = pos;
            pos2 = Vector3i.ZERO;
            FMT.info("Set frame pos1 ").stress(pos1).tell(source);
        } else if (pos2 == Vector3i.ZERO){
            pos2 = pos;
            FMT.info("Set frame pos2 ").stress(pos2).tell(source);
        } else if (origin == Vector3i.ZERO) {
            origin = pos;
            FMT.info("Set origin ").stress(pos2).tell(source);
        } else {
            pos1 = Vector3i.ZERO;
            pos2 = Vector3i.ZERO;
            origin = Vector3i.ZERO;
            FMT.info("Reset pos1 & pos2 & origin").tell(source);
        }
    }

    public void addFrame(Player source, int duration) {
        addFrame(source, true, duration);
    }

    public void addFrame(Player source, boolean ignoreAir, int duration) {
        if (pos1 == Vector3i.ZERO) {
            FMT.stress("pos1").error(" has not been set!").tell(source);
            return;
        }
        if (pos2 == Vector3i.ZERO) {
            FMT.stress("pos2").error(" has not been set!").tell(source);
            return;
        }

        FMT.info("Adding frame ").stress(frames.size()).tell(source);
        Frame frame = SchemFrame.at(source.getWorld(), pos1, pos2, origin, duration);
        frames.add(frame);
    }

    public void setFrame(Player source, int number, int duration) {
        setFrame(source, number, true, duration);
    }

    public void setFrame(Player source, int number, boolean ignoreAir, int duration) {
        if (number < 0 || number >= frames.size()) {
            FMT.error("Frame number must be in range: ").stress("%s - %s", 0, frames.size() - 1).tell(source);
            return;
        }

        FMT.info("Setting frame ").stress(number).tell(source);
        Frame frame = SchemFrame.at(source.getWorld(), pos1, pos2, origin, duration);
        frames.set(number, frame);
    }

    public void goToEnd(Player player) {
        goToFrame(player, frames.size() - 1);
    }

    public void goToFrame(Player player, int number) {
        if (number < 0 || number >= frames.size()) {
            FMT.error("Frame number must be in range: ").stress("%s - %s", 0, frames.size() - 1).tell(player);
            return;
        }

        FMT.info("Jumping to frame ").stress(number).tell(player);
        World world = player.getWorld();
        Vector3i origin = pos1;
        Frame.History history = null;
        for (int i = 0; i < number; i++) {
            if (history != null) {
                history.apply(world, origin, BlockChangeFlag.NONE);
            }
            history = frames.get(i).apply(world, origin, BlockChangeFlag.NONE);
        }
    }
}
