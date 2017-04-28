package me.dags.animation;

/**
 * @author dags <dags@dags.me>
 */
public interface SequenceProvider<T> {

    String getId();

    Sequence<T> getSequence();
}
