package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.handler.AnimationHandler;
import me.dags.animation.util.Sequence;
import me.dags.animation.util.SequenceProvider;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class FrameRecorder implements SequenceProvider<Frame> {

    private final List<Frame> frames = new ArrayList<>();
    private final ItemType wand;

    private AnimationHandler tester = AnimationHandler.builder().build();
    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;
    private Vector3i origin = Vector3i.ZERO;

    public FrameRecorder(ItemType wand) {
        this.wand = wand;
    }

    public ItemType getWand() {
        return wand;
    }

    public Vector3i getOrigin() {
        return origin;
    }

    public AnimationHandler getTester() {
        return tester;
    }

    public void setTester(AnimationHandler handler) {
        tester = handler;
    }

    @Override
    public String getId() {
        return "recorder";
    }

    @Override
    public Sequence<Frame> getSequence() {
        return Sequence.of(getFrames());
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
        if (number < 0 || number >= frames.size()) {
            FMT.error("Frame number must be in range: ").stress("%s - %s", 0, frames.size() - 1).tell(source);
            return;
        }

        FMT.info("Setting frame ").stress(number).tell(source);
        Frame frame = SchemFrame.at(source.getWorld(), pos1, pos2, origin, duration);
        frames.set(number, frame);
    }

    public void setDuration(Player source, int index, int duration) {
        if (index < 0 || index >= frames.size()) {
            FMT.error("Frame number must be in range: ").stress("%s - %s", 0, frames.size() - 1).tell(source);
            return;
        }

        Frame current = frames.get(index);
        if (current.getDuration() == duration) {
            FMT.error("Frame ").stress(index).error(" already has a duration of ").stress(duration).tell(source);
            return;
        }

        FMT.info("Setting frame ").stress(index).info(" duration to ").stress(duration);
        DataContainer container = current.toContainer().set(DataQuery.of("duration"), duration);
        Frame frame = SchemFrame.read(container);
        frames.set(index, frame);
    }

    public void setDuration(Player source, int duration) {
        FMT.info("Setting all frame durations to ").stress(duration).tell(source);

        for (int i = 0; i < frames.size(); i++) {
            Frame current = frames.get(i);
            if (current.getDuration() != duration) {
                DataContainer container = current.toContainer().set(DataQuery.of("duration"), duration);
                Frame frame = SchemFrame.read(container);
                frames.set(i, frame);
            }
        }
    }

    public void loadLast(Player player) {
        loadFrame(player, frames.size() - 1);
    }

    public void loadFrame(Player player, int number) {
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
