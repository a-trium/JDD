package chapter3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.function.Function;

import static chapter3.FunctionalInterfaces.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Chapter3Spec {

    List<Integer> ns;
    List<Apple> inventory;

    @Before
    public void setup() {
        ns = Arrays.asList(1, 2, 3, 4);
        inventory = Arrays.asList(
                new Apple(80,"green"),
                new Apple(155, "red"),
                new Apple(120, "red")
        );
    }

    @Test
    public void test_filter() {
        List<Integer> even = filter(ns, n -> n % 2 == 0);
        for(int n : even) assertTrue(n % 2 == 0);
    }

    @Test
    public void test_forEach() {
        forEach(ns, n -> System.out.println(n));
    }

    @Test
    public void test_map() {
        List<Integer> doubled = map(ns, n -> n * 2);
        assertEquals(doubled, Arrays.asList(2, 4, 6, 8));
    }

    @Test
    public void test_local_var() {
        int a = 3;
        forEach(ns, n -> System.out.println(n + a));
        // a = 5;
    }

    @Test
    public void test_method_ref() {
        List<String> str = Arrays.asList("a", "b" ,"A", "B");
        str.sort(String::compareToIgnoreCase);
        assertEquals(Arrays.asList("a", "A", "b", "B"), str);
    }

    @Test
    public void test_constructor_ref() {
        Supplier<Apple> c1 = Apple::new;
        Apple a1 = c1.get();

        Function<Integer, Apple> c2 = Apple::new;
        Apple a2 = c2.apply(100);

        List<Integer> weights = Arrays.asList(7, 3, 4, 10);
        List<Apple> apples = map(weights, Apple::new);

        BiFunction<Integer, String, Apple> c3 = Apple::new;
        Apple a3 = c3.apply(15, "green");
    }

    @Test
    public void test_practical_method_ref() {
        inventory.sort(Comparator.comparing(Apple::getWeight));

        List<Integer> weights = inventory.
                stream().
                map(Apple::getWeight).
                collect(Collectors.toList());

        assertEquals(Arrays.asList(80, 120, 155), weights);
    }

    @Test
    public void test_composed_comparator() {
        inventory.sort(
                Comparator.comparing(Apple::getWeight)
                .reversed()
        );

        List<Integer> weights = inventory.
                stream().
                map(Apple::getWeight).
                collect(Collectors.toList());

        assertEquals(Arrays.asList(155, 120, 80), weights);
    }

    @Test
    public void test_composed_predicate() {
        java.util.function.Predicate<Apple> redApple = a -> "red".equals(a.getColor());
        java.util.function.Predicate<Apple> notRedApple = redApple.negate();
        java.util.function.Predicate<Apple> redAndHeavyOrGreenApple =
                redApple
                        .and(a -> a.getWeight() > 120)
                        .or(a -> "green".equals(a.getColor()));


        long redAppleCount = inventory
                .stream()
                .filter(redApple)
                .count();

        assertEquals(2L, redAppleCount);

        long noRedAppleCount = inventory
                .stream()
                .filter(notRedApple)
                .count();

        assertEquals(1L, noRedAppleCount);
    }

    @Test
    public void test_tri_function() {
        Function<String, Function<String, String>> addHeader =
                text -> (header -> text + header);

        Function<String, Function<String, String>> addFooter =
                text -> (footer -> footer + text);
    }

    @Test
    public void test_composed_function() {
        Function<String, String> addHeader = Letter::addHeader;
        Function<String, String> addFooter = Letter::addFooter;
        Function<String, String> addHeaderAndFooter =
                addHeader.andThen(addFooter);

        assertEquals("headertextfooter", addHeaderAndFooter.apply("text"));

    }
}
