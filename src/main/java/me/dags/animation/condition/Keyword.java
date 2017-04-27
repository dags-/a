package me.dags.animation.condition;

/**
 * @author dags <dags@dags.me>
 */
public class Keyword implements Condition<String> {

    private final String keyword;
    private final String id;

    public Keyword(String id, String keyword) {
        this.keyword = keyword;
        this.id = id;
    }

    @Override
    public boolean test(String s) {
        return s.equalsIgnoreCase(keyword);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword keyword = (Keyword) o;

        return id != null ? id.equals(keyword.id) : keyword.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
