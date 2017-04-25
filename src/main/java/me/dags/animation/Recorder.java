package me.dags.animation;

import com.flowpowered.math.vector.Vector3i;
import me.dags.animation.animation.*;
import me.dags.animation.frame.Frame;
import me.dags.animation.frame.FullFrame;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
public class Recorder {

    private final List<Frame> frames = new ArrayList<>();
    private Vector3i pos1 = Vector3i.ZERO;
    private Vector3i pos2 = Vector3i.ZERO;

    private Vector3i min = Vector3i.ZERO;
    private Vector3i max = Vector3i.ZERO;

    private Task current = null;

    @Listener
    public void onInteract(InteractItemEvent.Secondary event, @Root Player player) {
        if (event.getItemStack().getType() == ItemTypes.STICK && event.getInteractionPoint().isPresent()) {
            Vector3i pos = event.getInteractionPoint().get().toInt();
            if (pos1 == Vector3i.ZERO) {
                pos1 = pos;
                pos2 = Vector3i.ZERO;
                FMT.info("Set pos1 ").stress(pos1).tell(player);
            } else if (pos2 == Vector3i.ZERO){
                pos2 = pos;
                FMT.info("Set pos2 ").stress(pos2).tell(player);
                min = pos1.min(pos2);
                max = pos1.max(pos2);
            } else {
                FMT.info("Reset pos1 & pos2").tell(player);
                pos1 = Vector3i.ZERO;
                pos2 = Vector3i.ZERO;
                min = Vector3i.ZERO;
                max = Vector3i.ZERO;
            }
        }
    }

    @Command(alias = "save", parent = "frame")
    public void frame(@Caller Player player) {
        frame(player, -1);
    }

    @Command(alias = "save", parent = "frame")
    public void frame(@Caller Player player, @One("duration") int duration) {
        if (min == Vector3i.ZERO || max == Vector3i.ZERO) {
            FMT.error("Frame region not set").tell(player);
            return;
        }

        FMT.info("Adding frame ").stress(frames.size()).tell(player);

        World world = player.getWorld();
        Schematic schematic = Schematic.builder()
                .paletteType(BlockPaletteTypes.LOCAL)
                .volume(world.createArchetypeVolume(min, max, min))
                .build();

        frames.add(new FullFrame(schematic, duration));
    }

    @Command(alias = "clear", parent = "frame")
    public void clear(@Caller Player player) {
        stop(player);
        frames.clear();
    }

    @Command(alias = "reset", parent = "frame")
    public void reset(@Caller Player player) {
        if (frames.size() == 0) {
            FMT.error("No frames exist yet").tell(player);
            return;
        }

        FMT.info("Resetting...").tell(player);
        Frame root = frames.get(0);
        root.newInstance().reset(player.getWorld(), min, BlockChangeFlag.NONE);
    }

    @Command(alias = "test", parent = "frame")
    public void test(@Caller Player player) {
        test(player, 20);
    }

    @Command(alias = "test", parent = "frame")
    public void test(@Caller Player player, int interval) {
        List<Vector3i> path = new ArrayList<>();
        Vector3i pos = new Vector3i();
        for (int i = 0; i < 20; i++) {
            path.add(pos = pos.add(i, 0, i));
        }

        Animation simple = new SimpleAnimation(Sequence.of(frames, Frame::newInstance));
        Animation looper = new RepeatAnimation(simple);
        Animation mover = new MovingAnimation(looper, Sequence.of(path));

        AnimationTask task = AnimationTask.builder()
                .world(player.getWorld())
                .animation(mover)
                .origin(min)
                .build();

        FMT.info("Testing...").tell(player);
        current = Task.builder().execute(task).intervalTicks(1).submit(Animator.instance);
    }

    @Command(alias = "stop", parent = "frame")
    public void stop(@Caller Player player) {
        if (current != null) {
            FMT.info("Stopping animation...").tell(player);
            current.cancel();
            reset(player);
        } else {
            FMT.error("No animation running").tell(player);
        }
    }
}
