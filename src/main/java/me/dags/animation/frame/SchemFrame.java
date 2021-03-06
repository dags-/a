package me.dags.animation.frame;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;
import org.spongepowered.api.world.schematic.Schematic;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */
public class SchemFrame implements Frame {

    private final Schematic schematic;
    private final boolean air;
    private final int duration;

    public SchemFrame(Schematic schematic) {
        Optional<Integer> duration = schematic.getMetadata().getInt(DataQuery.of("duration"));
        Optional<Boolean> air = schematic.getMetadata().getBoolean(DataQuery.of("air"));

        if (!duration.isPresent()) {
            throw new UnsupportedOperationException("No duration specified for the frame!");
        }

        this.schematic = schematic;
        this.duration = duration.get();
        this.air = air.orElse(false);
    }

    @Override
    public History apply(World world, Vector3i position, BlockChangeFlag flag) {
        History history = new SchemHistory();
        Frame.paste(world, schematic, position, flag, air, history);
        return history;
    }

    @Override
    public Vector3i getMin() {
        return schematic.getBlockMin();
    }

    @Override
    public Vector3i getMax() {
        return schematic.getBlockMax();
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public DataContainer toContainer() {
        return DataTranslators.SCHEMATIC.translate(schematic);
    }

    public static Frame at(World world, Vector3i pos1, Vector3i pos2, Vector3i origin, int duration, boolean air) {
        Vector3i min = pos1.min(pos2);
        Vector3i max = pos1.max(pos2);
        ArchetypeVolume volume = world.createArchetypeVolume(min, max, origin);
        Schematic schematic = Schematic.builder()
                .paletteType(BlockPaletteTypes.LOCAL)
                .metadata(new MemoryDataContainer()
                        .set(DataQuery.of("duration"), duration)
                        .set(DataQuery.of("air"), air))
                .volume(volume)
                .build();

        return new SchemFrame(schematic);
    }

    public static List<Frame> readAll(InputStream inputStream) throws Exception {
        return readAll(DataFormats.NBT.readFrom(inputStream));
    }

    public static List<Frame> readAll(DataView view) throws Exception {
        Optional<List<DataView>> views = view.getViewList(DataQuery.of("sequenceProvider"));
        if (views.isPresent()) {
            ImmutableList.Builder<Frame> frames = ImmutableList.builder();
            for (DataView data : views.get()) {
                frames.add(read(data));
            }
            return frames.build();
        }
        return Collections.emptyList();
    }

    public static Frame read(InputStream inputStream) throws Exception {
        return read(DataFormats.NBT.readFrom(inputStream));
    }

    public static Frame read(DataView view) {
        Schematic schematic = DataTranslators.SCHEMATIC.translate(view);
        return new SchemFrame(schematic);
    }
}
