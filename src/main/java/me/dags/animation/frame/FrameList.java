package me.dags.animation.frame;

import me.dags.animation.Animator;
import me.dags.animation.Sequence;
import me.dags.animation.SequenceProvider;
import org.spongepowered.api.CatalogType;

/**
 * @author dags <dags@dags.me>
 */
public class FrameList implements CatalogType, SequenceProvider<Frame> {

    private final String id;

    public FrameList(String id) {
        this.id = id;
    }

    @Override
    public Sequence<Frame> getSequence()  {
        return Sequence.of(Animator.getFrameRegistry().getFrames(this));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrameList frameList = (FrameList) o;
        return id != null ? id.equals(frameList.id) : frameList.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
