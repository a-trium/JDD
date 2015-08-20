package chapter3;

import java.util.ArrayList;
import java.util.List;

public class FunctionalInterfaces {
    @FunctionalInterface
    interface Callable<V> {
        V call();
    }

    @FunctionalInterface
    interface Comparator<T> {
        int compare(T o1, T o2);
    }

    @FunctionalInterface
    interface Consumer<T> {
        void accept(T t);
    }

    @FunctionalInterface
    interface Predicate<T> {
        boolean test(T t);
    }

    @FunctionalInterface
    interface Runnable {
        void run();
    }

    @FunctionalInterface
    interface Function<T, R> {
        R apply(T t);
    }

    @FunctionalInterface
    interface Supplier<T> {
        T get();
    }

    @FunctionalInterface
    interface UnaryOperator<T> {
        T op(T t);
    }

    @FunctionalInterface
    interface BiPredicate<L, R> {
        boolean test(L l, R r);
    }

    public static <T> List<T> filter(List<T> ts, Predicate<T> p) {
        List<T> filtered = new ArrayList<>();

        for(T t : ts) if (p.test(t)) filtered.add(t);

        return filtered;
    }

    public static <T> void forEach(List<T> ts, Consumer<T> c) {
        for(T t : ts) c.accept(t);
    }

    public static <T, R> List<R> map(List<T> ts, Function<T, R> f) {
        List<R> rs = new ArrayList<>();
        for(T t : ts) rs.add(f.apply(t));

        return rs;
    }
}
