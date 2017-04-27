package me.dags.animation.util;

/**
 * @author dags <dags@dags.me>
 */
public interface SequenceProvider<T> {

    String getId();

    Sequence<T> getSequence();
}
