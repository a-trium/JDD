package chapter5;

import chapter4.Dish;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Chapter5Spec {

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
    public void test_distinct() {
        List<Integer> ns = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        long distinctCount = ns.stream().distinct().count();
        assertEquals(4, distinctCount);
    }

    @Test
    public void test_first_two_meat() {
        List<Dish> twoMeat = menu
            .stream()
            .filter(d -> Dish.Type.MEAT == d.getType())
            .limit(2)
            .collect(toList());

        assertEquals(2, twoMeat.size());

        twoMeat.stream().forEach(d -> assertEquals(Dish.Type.MEAT, d.getType()));
    }

    @Test
    public void test_get_unique_char() {

        List<String> uniqChars = words
            .stream()
            .map(w -> w.toLowerCase().split(""))
            .flatMap(Arrays::stream)
            .distinct()
            .collect(toList());

        assertEquals(14, uniqChars.size());
    }

    @Test
    public void test_zip() {
        List<Integer> xs = Arrays.asList(1, 2, 3);
        List<Integer> ys = Arrays.asList(4, 5);

        List<int[]> pairs = xs
            .stream()
            .flatMap(x -> ys.stream().map(y -> new int[]{x, y}))
            .collect(toList());

        assertEquals(6, pairs.size());
    }

    @Test
    public void test_findAny() {
        menu.stream()
            .filter(Dish::isVegetarian)
            .findAny()
            .ifPresent(d -> System.out.println(d.getName()));
    }

    @Test
    public void test_reduce() {
        long product = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .reduce(1, (a, b) -> a * b);

        assertEquals(120, product);

        long sum = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .reduce(0, Integer::sum);

        assertEquals(15, sum);
    }

    @Test
    public void test_max_min() {
        Optional<Integer> max = Arrays.asList(0)
                                      .stream()
                                      .reduce(Integer::max);

        if (max.isPresent()) assertEquals(Integer.valueOf(0), max.get());
    }

    @Test
    public void test_transaction_example() {
        List<Transaction> elevenTx = transactions
            .stream()
            .filter(tx -> 2011 == tx.getYear())
            .sorted(Comparator.comparing(Transaction::getValue))
            .collect(toList());

        List<String> uniqCities = transactions
            .stream()
            .map(tx -> tx.getTrader().getCity())
            .distinct()
            .collect(toList());

        List<Trader> livesInCambridge = transactions
            .stream()
            .map(tx -> tx.getTrader())
            .filter(p -> "Cambridge".equals(p.getCity()))
            .sorted(Comparator.comparing(Trader::getName))
            .collect(toList());

        List<String> traderNames = transactions
            .stream()
            .map(tx -> tx.getTrader().getName())
            .distinct()
            .sorted()
            .collect(toList());

        Optional<Trader> livesInMilan = transactions
            .stream()
            .map(t -> t.getTrader())
            .filter(p -> "Milan".equals(p.getCity()))
            .findAny();

        List<Integer> txValuesInCambridge = transactions
            .stream()
            .filter(tx -> "Cambridge".equals(tx.getTrader().getCity()))
            .map(tx -> tx.getValue())
            .collect(toList());

        OptionalInt maxTxValue = transactions
            .stream()
            .mapToInt(tx -> tx.getValue())
            .max();

        Optional<Integer> minTxValue = transactions
            .stream()
            .map(tx -> tx.getValue())
            .min(Integer::compare);

        Optional<Transaction> minTx = transactions
            .stream()
            .min(Comparator.comparing(Transaction::getValue));
    }
}


