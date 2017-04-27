package me.dags.animation.command;

import me.dags.animation.Animator;
import me.dags.animation.animation.AnimationFactory;
import me.dags.animation.animation.AnimationHandler;
import me.dags.animation.frame.Recorder;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.One;
import me.dags.commandbus.format.FMT;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

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
            FMT.info("Frame recorder wand bound to ").stress(inHand.get().getName()).tell(player);
            Animator.createRecorder(player.getUniqueId(), inHand.get());
        } else {
            FMT.error("You must be holding an item to use as your wand").tell(player);
        }
    }

    @Command(alias = "add", parent = "frame")
    public void add(@Caller Player player, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().addFrame(player,  duration);
        }
    }

    @Command(alias = "replace", parent = "frame")
    public void replace(@Caller Player player, @One("index") int index, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().setFrame(player, index, duration);
        }
    }

    @Command(alias = "duration", parent = "frame")
    public void setDuration(@Caller Player player, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().setDuration(player, duration);
        }
    }

    @Command(alias = "duration", parent = "frame")
    public void setDuration(@Caller Player player, @One("index") int index, @One("duration") int duration) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().setDuration(player, index, duration);
        }
    }

    @Command(alias = "goto", parent = "frame")
    public void goTo(@Caller Player player, @One("index") int index) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().goToFrame(player, index);
        }
    }

    @Command(alias = "last", parent = "frame")
    public void last(@Caller Player player) {
        Optional<Recorder> recorder = getRecorder(player);
        if (recorder.isPresent()) {
            recorder.get().goToEnd(player);
        }
    }

    @Command(alias = "test", parent = "frame")
    public void test(@Caller Player player, @One("animation") AnimationFactory factory) {
        Optional<Recorder> recorder = getRecorder(player);

        if (recorder.isPresent()) {
            FMT.info("Starting test animation...").tell(player);
            AnimationHandler handler = AnimationHandler.builder()
                    .frames(recorder.get().getFrames())
                    .origin(recorder.get().getOrigin())
                    .factory(factory)
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
