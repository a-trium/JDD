package chapter6;

import chapter4.Dish;
import chapter5.Trader;
import chapter5.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Chapter6Spec {

    List<Dish> menu;
    List<String> words;
    Trader raoul;
    Trader mario;
    Trader alan;
    Trader brian;

    List<Transaction> transactions;

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

        words = Arrays.asList("Java8", "Lambdas", "In", "Action");

        raoul = new Trader("Raoul", "Cambridge");
        mario = new Trader("Mario","Milan");
        alan = new Trader("Alan","Cambridge");
        brian = new Trader("Brian","Cambridge");

        transactions = Arrays.asList(
            new Transaction(brian, 2011, 300),
            new Transaction(raoul, 2012, 1000),
            new Transaction(raoul, 2011, 400),
            new Transaction(mario, 2012, 710),
            new Transaction(mario, 2012, 700),
            new Transaction(alan, 2012, 950)
        );
    }

    @Test
    public void test_summary() {
        long howMany = menu
            .stream().collect(Collectors.counting()); // same as count()

        assertEquals(9, howMany);

        int totalCalories = menu
            .stream().collect(Collectors.summingInt(Dish::getCalories));

        assertEquals(4200, totalCalories);

        double avgCalories = menu
            .stream().collect(Collectors.averagingDouble(Dish::getCalories));

        IntSummaryStatistics stat = menu
            .stream().collect(Collectors.summarizingInt(Dish::getCalories));

        System.out.println(stat);
    }

    @Test
    public void test_joining() {
        String shortMenu = menu
            .stream()
            .map(d -> d.getName())
            .collect(joining());

        assertEquals("porkbeefchickenfrench friesriceseason fruitpizzaprawnssalmon", shortMenu);

        String shortMenuWithComma = menu
            .stream()
            .map(d -> d.getName())
            .collect(joining(", "));

        assertEquals("pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon", shortMenuWithComma);
    }

    @Test
    public void test_reduce_basic() {
        int totalCalories = menu
            .stream().collect(reducing(
                0, Dish::getCalories, (c1, c2) -> c1 + c2
            ));

        assertEquals(4200, totalCalories);

        int totalCalories2 = menu
            .stream().collect(reducing(
                0, Dish::getCalories, Integer::sum
            ));

        assertEquals(4200, totalCalories2);

        int totalCalories3 = menu
            .stream().mapToInt(Dish::getCalories).sum();

        assertEquals(4200, totalCalories3);
    }

    @Test
    public void test_grouping() {
        Map<Dish.Type, List<Dish>> dishesByType = menu
            .stream().collect(groupingBy(Dish::getType));

        assertEquals(
            new HashSet<Dish.Type>(Arrays.asList(Dish.Type.values())),
            dishesByType.keySet());
    }

    @Test
    public void test_grouping2() {
        Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
            menu.stream().collect(groupingBy((Dish d) -> {
                if (d.getCalories() <= 400)
                    return CaloricLevel.DIET;
                else if (d.getCalories() <= 700)
                    return CaloricLevel.NORMAL;
                else
                    return CaloricLevel.FAT;
            }));

        List<Dish> dietDishes = dishesByCaloricLevel.get(CaloricLevel.DIET);
        assertEquals(4, dietDishes.size());

        // System.out.println(dishesByCaloricLevel);
        // {DIET=[chicken, rice, season fruit, prawns], FAT=[pork], NORMAL=[beef, french fries, pizza, salmon]}
    }

    @Test
    public void test_nested_grouping() {
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeAndCaloricLevel =
            menu.stream().collect(
                groupingBy(Dish::getType,
                    groupingBy(d -> {
                        if (d.getCalories() <= 400)
                            return CaloricLevel.DIET;
                        else if (d.getCalories() <= 700)
                            return CaloricLevel.NORMAL;
                        else
                            return CaloricLevel.FAT;
                    })));

        assertEquals(3, dishesByTypeAndCaloricLevel.get(Dish.Type.MEAT).size());

        // System.out.println(dishesByTypeAndCaloricLevel);
        // {MEAT={DIET=[chicken], FAT=[pork], NORMAL=[beef]}, FISH={DIET=[prawns], NORMAL=[salmon]}, OTHER={DIET=[rice, season fruit], NORMAL=[french fries, pizza]}
    }

    @Test
    public void test_grouping3() {
        Map<Dish.Type, Long> typesCount =
            menu.stream().collect(groupingBy(Dish::getType, counting()));

        assertEquals(3L, (long) typesCount.get(Dish.Type.MEAT));
    }

    @Test
    public void test_grouping_optional() {
        Map<Dish.Type, Optional<Dish>> mostCaloricByType =
            menu.stream().collect(groupingBy(
                Dish::getType,
                maxBy(Comparator.comparingInt(Dish::getCalories))));
    }

}


