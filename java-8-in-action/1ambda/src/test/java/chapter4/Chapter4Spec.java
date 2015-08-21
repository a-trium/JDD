package chapter4;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Chapter4Spec {

    List<Dish> menu;

    @Before
    public void setup() {
        menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH));
    }

    @Test
    public void test_basic_stream() {
        List<String> threeHighCaloricDishNames =
                menu.stream()
                        .filter(d -> d.getCalories() > 300)
                        .map(Dish::getName)
                        .limit(3)
                        .collect(toList());

        assertEquals(Arrays.asList("pork", "beef", "chicken"), threeHighCaloricDishNames);
    }

    @Test(expected = IllegalStateException.class)
    public void test_cannot_reuse_stream() {
        Stream<String> ss = Arrays.asList("Java8", "In", "Action").stream();
        ss.forEach(System.out::println);
        ss.forEach(System.out::println);
    }
}
