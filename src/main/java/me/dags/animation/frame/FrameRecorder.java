package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.PositionRecorder;
import me.dags.animation.Sequence;
import me.dags.animation.SequenceProvider;
import me.dags.animation.handler.AnimationHandler;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
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
public class FrameRecorder extends PositionRecorder implements SequenceProvider<Frame> {

    private final List<Frame> frames = new ArrayList<>();

    private AnimationHandler tester = AnimationHandler.builder().build();
    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;
    private Vector3i origin = Vector3i.ZERO;

    public FrameRecorder(ItemType wand) {
        super(wand);
    }

    @Override
    public void setPos(Player player, Vector3i pos) {
        if (getSize() == 3) {
            reset();
            pos1 = Vector3i.ZERO;
            pos2 = Vector3i.ZERO;
            origin = Vector3i.ZERO;
        } else {
            super.setPos(player, pos);
            Optional<Vector3i> last = getLast();

            if (last.isPresent()) {
                if (getSize() == 1) {
                    pos1 = last.get();
                } else if (getSize() == 2) {
                    pos2 = last.get();
                } else {
                    origin = last.get();
                }
            }
        }
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
