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

import static java.util.Comparator.comparingInt;
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
                maxBy(comparingInt(Dish::getCalories))));
    }

    @Test
    public void test_grouping_collectingAndThen() {
        Map<Dish.Type, Dish> mostCaloricByType =
            menu.stream()
                .collect(groupingBy(Dish::getType,
                    collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));

        System.out.println(mostCaloricByType);

        assertEquals("pizza", mostCaloricByType.get(Dish.Type.OTHER).getName());
    }

    @Test
    public void test_mapping() {
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
            menu.stream()
                .collect(groupingBy(Dish::getType, mapping((Dish d) -> {
                    if (d.getCalories() <= 400)
                        return CaloricLevel.DIET;
                    else if (d.getCalories() <= 700)
                        return CaloricLevel.NORMAL;
                    else
                        return CaloricLevel.FAT;
                }, toSet()))); // or toCollection(HashSet::new)

        // output:
        // {MEAT=[FAT, DIET, NORMAL], OTHER=[DIET, NORMAL], FISH=[DIET, NORMAL]}
    }

    @Test
    public void test_partitioningBy1() {
        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
            menu.stream()
                .collect(partitioningBy(
                    Dish::isVegetarian,
                    collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));

        assertEquals("pork", mostCaloricPartitionedByVegetarian.get(false).getName());
        assertEquals("pizza", mostCaloricPartitionedByVegetarian.get(true).getName());
    }

    @Test
    public void test_prime_number() {
        int n = 10; // 2, 3, 5, 7
        Map<Boolean, List<Integer>> primes = PrimeNumber.partitionPrimes(10);

        assertEquals(4, primes.get(true).size());  // 2, 3, 5, 7
        assertEquals(5, primes.get(false).size()); // 4, 6, 8, 9, 10
    }

    @Test
    public void test_custom_collector() {
        // same as
        // menu.stream().collect(toList());
        List<Dish> dishes =
            menu.stream()
                .collect(new ToListCollector<Dish>());

        assertEquals(9, dishes.size());
    }

    @Test
    public void test_custom_collector2() {
        List<Dish> dishes =
            menu.stream()
                .collect(
                    ArrayList::new,
                    List::add,
                    List::addAll
                );

        assertEquals(9, dishes.size());
    }

    @Test
    public void test_perf_prime_collector() {
        System.out.println("using PrimeNumberCollector: " + testPerformance(() -> {
            PrimeNumber.partitionPrimesUsingCollector(1000000);
        }));

        System.out.println("without PrimeNumberCollector: " + testPerformance(() -> {
            PrimeNumber.partitionPrimes(1000000);
        }));
    }

    public long testPerformance(Runnable callback) {
        long fastest = Long.MAX_VALUE;

        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();

            callback.run();

            long duration = (System.nanoTime() - start) / 1000000; /* ms */

            if (duration < fastest) fastest = duration;
        }

        return fastest;
    }

}


