package me.dags.animation.context;

/**
 * @author dags <dags@dags.me>
 */
public class Keyword implements Condition<String> {

    private final String keyword;

    public Keyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean test(String s) {
        return s.equalsIgnoreCase(keyword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keyword keyword1 = (Keyword) o;

        return keyword != null ? keyword.equals(keyword1.keyword) : keyword1.keyword == null;

    }

    @Override
    public int hashCode() {
        return keyword != null ? keyword.hashCode() : 0;
    }
}
