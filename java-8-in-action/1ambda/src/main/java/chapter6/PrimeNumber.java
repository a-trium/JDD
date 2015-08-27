package chapter6;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.partitioningBy;

public class PrimeNumber {

    public static boolean isPrime(int candidate) {
        int root = (int) Math.sqrt((double) candidate);

        return IntStream.rangeClosed(2, root)
            .noneMatch(i -> candidate % i == 0);
    }

    public static boolean isPrime(List<Integer> primes, int candidate) {
        int root = (int) Math.sqrt((double) candidate);

        return takeWhile(primes, i -> i < root)
            .stream()
            .noneMatch(p -> candidate % p == 0);
    }

    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream
            .rangeClosed(2, n)
            .boxed()
            .collect(partitioningBy(PrimeNumber::isPrime));
    }

    public static Map<Boolean, List<Integer>> partitionPrimesUsingCollector(int n) {
        return IntStream
            .rangeClosed(2, n)
            .boxed()
            .collect(new PrimeNumberCollector());
    }

    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
        int i = 0;

        for (A a : list) {
            if (!p.test(a))
                return list.subList(0, i);

            i ++;
        }

        return list;
    }
}
