package me.dags.animation;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author dags <dags@dags.me>
 */
public class Utils {

    public static <K, V> V ensure(Map<K, V> map, K key, Supplier<V> supplier) {
        V value = map.get(key);
        if (value == null) {
            map.put(key, value = supplier.get());
        }
        return value;
    }
}
