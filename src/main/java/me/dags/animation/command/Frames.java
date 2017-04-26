package me.dags.animation.command;

import me.dags.animation.AnimationHandler;
import me.dags.animation.Animator;
import me.dags.animation.Recorder;
import me.dags.animation.Sequence;
import me.dags.animation.animation.Animation;
import me.dags.animation.animation.AnimationTask;
import me.dags.animation.animation.PushPullAnimation;
import me.dags.animation.animation.SimpleAnimation;
import me.dags.animation.frame.Frame;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class Frames {

    private Optional<Recorder> getRecorder(Player player) {
        Optional<Recorder> recorder = Animator.getRecorder(player.getUniqueId());
        if (!recorder.isPresent()) {
            FMT.error("You are not currently recording").tell(player);
        }
        return recorder;
    }

    @Command(alias = "record", parent = "frame")
    public void record(@Caller Player player) {
        Optional<ItemType> inHand = player.getItemInHand(HandTypes.MAIN_HAND).map(ItemStack::getItem);
        if (inHand.isPresent()) {
            FMT.info("Starting new recorder...").tell(player);
            Animator.createRecorder(player.getUniqueId(), inHand.get());
        } else {
            FMT.error("You must be holding an item to use as your wand").tell(player);
        }
    }

    @Command(alias = "add", parent = "frame")
    public void add(@Caller Player player, @One("duration") int duration) {
        add(player, true, duration);
    }

    @Command(alias = "add", parent = "frame")
    public void add(@Caller Player player, @One("ignore air") boolean ignoreAir, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().addFrame(player, ignoreAir, duration);
        }
    }

    @Command(alias = "set", parent = "frame")
    public void set(@Caller Player player, @One("frame_number") int number, @One("duration") int duration) {
        set(player, number, true, duration);
    }

    @Command(alias = "set", parent = "frame")
    public void set(@Caller Player player, @One("frame_number") int number, @One("ignore air") boolean ignoreAir, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().setFrame(player, number, ignoreAir, duration);
        }
    }

    @Command(alias = "test", parent = "frame")
    public void test(@Caller Player player) {
        Optional<Recorder> recorder = getRecorder(player);

        if (recorder.isPresent()) {
            FMT.info("Starting test animation...").tell(player);
            AnimationHandler handler = AnimationHandler.builder()
                    .frames(recorder.get().getFrames())
                    .origin(recorder.get().getOrigin())
                    .build();

            recorder.get().setTester(handler);
            handler.start(player.getWorld());
        }
    }

    @Command(alias = "stop", parent = "frame")
    public void stop(@Caller Player player) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            FMT.info("Stopping test animation").tell(player);
            AnimationHandler handler = recorder.get().getTester();
            handler.pause();
            handler.complete();
        }
    }
}