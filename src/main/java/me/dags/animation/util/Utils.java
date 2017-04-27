package me.dags.animation.util;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class Utils {

    private static final Charset UTF8 = Charset.forName("utf8");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final PathMatcher JSON_MATCHER = FileSystems.getDefault().getPathMatcher("glob:*.json");

    public static boolean isJsonFile(Path path) {
        return JSON_MATCHER.matches(path.getFileName());
    }

    public static <K, V> V ensure(Map<K, V> map, K key, Supplier<V> supplier) {
        V value = map.get(key);
        if (value == null) {
            map.put(key, value = supplier.get());
        }
        return value;
    }

    public static <T> T get(JsonObject parent, String key, Function<JsonElement, T> mapper, T def) {
        JsonElement element = parent.get(key);
        if (element != null && !element.isJsonNull()) {
            return mapper.apply(element);
        }
        return def;
    }

    public static JsonElement read(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return new JsonParser().parse(reader);
        } catch (IOException e) {
            return JsonNull.INSTANCE;
        }
    }

    public static void write(JsonElement element, Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, UTF8)) {
                writer.write(GSON.toJson(element));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stream<Path> readDir(Path dir) {
        try {
            return Files.walk(dir);
        } catch (IOException e) {
            return Stream.empty();
        }
    }
}
