# Chapter 6

## Collector

`Collector` 인터페이스 구현은 스트림의 요소를 어떤식으로 도출할지 지정한다. 

- toList
- summingInt
- reducing 등

`Collectors` 에는 미리 정의된 컬렉터가 있다. 이 컬렉터는 크게 3 가지 기능으로 나눌 수 있는데, 

- 스트림의 요소를 하나의 값으로 리듀스하고 요약
- 요소 그룹화
- 요소 분할

## Reducing, Summary

```java
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
            .collect(Collectors.joining());

        assertEquals("porkbeefchickenfrench friesriceseason fruitpizzaprawnssalmon", shortMenu);

        String shortMenuWithComma = menu
            .stream()
            .map(d -> d.getName())
            .collect(Collectors.joining(", "));

        assertEquals("pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon", shortMenuWithComma);
    }
```

다음은 범용 리듀싱 연산의 예다. 이를 이용하면 이전에 나온 모든 컬렉터를 정의할 수 있다.

```java
    @Test
    public void test_reduce_basic() {
        int totalCalories = menu
            .stream().collect(reducing(
                0, Dish::getCalories, (c1, c2) -> c1 + c2
            ));

        assertEquals(4200, totalCalories);
    }
```

> 스트림 인터페이스의 `collect` 와 `reduce` 는 서로 같은 기능을 구현할 수 있다. 그러나 `collect` 메서드는 도출하려는 결과를 누적하는 컨테이너를 
바꾸도록 설계된 반면, `reduce` 메서드는 두 값을 하나로 도출하는 불변형 연산이다. 따라서 가변 컨테이너 관련 작업이면서 병렬성을 확보하려면 `collect` 메서드로 
리듀싱 연산을 구현하는 것이 바람직하다.

스트림 인터페이스를 이용하면 같은 기능도 여러가지 방법으로 구현할 수 있다. 따라서 문제에 특화된 해결책을 고르는 것이 바람직하다. 

```java
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

        // IntStream 을 이용하므로 가장 좋은 성능을 보임
        int totalCalories3 = menu
            .stream().mapToInt(Dish::getCalories).sum();

        assertEquals(4200, totalCalories3);
    }
```

## Grouping

```java

public enum CaloricLevel {
    DIET, NORMAL, FAT
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
```

중첩된 그룹화도 가능하다. `grouping` 은 분류 함수와 컬렉터를 인수로 받기 때문에, 스트림 항목을 분류할 두번째 기준을 정의하는 내부 `groupingBy` 를 
전달할 수 있다.

```java
    /*
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param classifier the classifier function mapping input elements to keys
     * @return a {@code Collector} implementing the group-by operation
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>>
    groupingBy(Function<? super T, ? extends K> classifier) {
        return groupingBy(classifier, toList());
    }
```

예를 들어

```java
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
```

`groupingBy` 의 두번째 인자에 말고 다양한 컬렉터를 사용할 수 있다.

```java
    @Test
    public void test_grouping_optional() {
        Map<Dish.Type, Optional<Dish>> mostCaloricByType =
            menu.stream().collect(groupingBy(
                Dish::getType,
                maxBy(comparingInt(Dish::getCalories))));
    }
```

다만 `maxBy` 를 이용하는 경우 맵의 값이 `Optional` 이 된다. 그러나 `Optional.empty` 에 해당되는 `Dish` 는 존재하지 않으므로 아래의 코드처럼 
`collectingAndThen` 을 이용해서 `Optional` 컨테이너를 벗길 수 있다.

```java
    @Test
    public void test_grouping_collectingAndThen() {
        Map<Dish.Type, Dish> mostCaloricByType =
            menu.stream()
                .collect(groupingBy(Dish::getType,
                    collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));

        System.out.println(mostCaloricByType);

        assertEquals("pizza", mostCaloricByType.get(Dish.Type.OTHER).getName());
    }
```

## Mapping

```java
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
```

`mapping` 은 입력 요소를 누적하기 전에 매핑 함수를 적용해서 다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환하는 역할을 한다. 

## Partitioning

```java
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
```

아래는 `partitioningBy` 를 이용해 소수(Prime Number) 를 구하는 예제다.

```java
public class PrimeNumber {

    public static boolean isPrime(int candidate) {
        int root = (int) Math.sqrt((double) candidate);

        return IntStream.rangeClosed(2, root)
            .noneMatch(i -> candidate % i == 0);
    }

    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream
            .rangeClosed(2, n)
            .boxed()
            .collect(partitioningBy(PrimeNumber::isPrime));
    }
}

@Test
public void test_prime_number() {
    int n = 10; // 2, 3, 5, 7
    Map<Boolean, List<Integer>> primes = PrimeNumber.partitionPrimes(10);

    assertEquals(4, primes.get(true).size());  // 2, 3, 5, 7
    assertEquals(5, primes.get(false).size()); // 4, 6, 8, 9, 10
}
```

## Collector Interface

```java
public interface Collector<T, A, R> {

    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    BinaryOperator<A> combiner();
    Function<A, R> finisher();
    Set<Characteristics> characteristics();

    public static<T, R> Collector<T, R, R> of(Supplier<R> supplier,
                                              BiConsumer<R, T> accumulator,
                                              BinaryOperator<R> combiner,
                                              Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = (characteristics.length == 0)
                                  ? Collectors.CH_ID
                                  : Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH,
                                                                           characteristics));
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, cs);
    }

    public static<T, A, R> Collector<T, A, R> of(Supplier<A> supplier,
                                                 BiConsumer<A, T> accumulator,
                                                 BinaryOperator<A> combiner,
                                                 Function<A, R> finisher,
                                                 Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(characteristics);
        Set<Characteristics> cs = Collectors.CH_NOID;
        if (characteristics.length > 0) {
            cs = EnumSet.noneOf(Characteristics.class);
            Collections.addAll(cs, characteristics);
            cs = Collections.unmodifiableSet(cs);
        }
        return new Collectors.CollectorImpl<>(supplier, accumulator, combiner, finisher, cs);
    }
    
    enum Characteristics {
        CONCURRENT,
        UNORDERED,
        IDENTITY_FINISH
    }
}
```

- `T` 는 수집될 스트림 항목의 제네릭 형식이다
- `A` 는 누적자 객체의 형식이다
- `R` 은 수집 연산 결과 객체의 형식 (대개 컬렉션)

예를 들어 `Stream<T>` 를 `List<T>` 로 수집하는 경우 다음과 같은 클래스로 정의할 수 있다. 

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

- `Supplier<A> supplier` 메서드는 새로운 결과 컨테이너를 만든다
- `BiConsumer<A, T> accumulator` 메서드는 결과 컨테이너에 요소를 추가한다. 
- `Function<A, R> finisher` 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환한다
- `BinaryOperator<A> combiner()` 메서드는 두 결과 컨테이너를 병합한다. 병렬 연산시 사용한다.
- `Set<Characteristics> characteristics()` 메서드는 컬렉터 연산 옵션을 지정한다. 스트림을 병렬로 리듀스할 것인지, 병렬로 
리듀스 한다면 어떤 최적화를 선택할지 등

<br/>

- `UNORDERED`: 리듀싱 결과는 스트림 요소의 방문 순서나, 누적 순서에 영향을 받지 않는다
- `CONCURRENT`: 다중 스레드에서 `accumulator` 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 
이 컬렉터는 병렬 리듀싱을 수행할 수 있으며 `UNORDERED` 를 함께 설정하지 않았을 경우 데이터가 소스가 정렬되지 않을 경우만 (e.g 집합) 병렬 리듀싱을 할 수 있다.
- `IDENTITY_FINISH`: `finisher` 메서드가 반환하는 것이 누적자 객체임을 알려준다.
 
이를 이용하면 아래처럼 커스텀 컬렉터를 정의할 수 있다.
 
```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(
            Characteristics.IDENTITY_FINISH,
            Characteristics.CONCURRENT
        ));
    }
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
```

커스텀 컬렉터를 정의하는 대신, `collect` 함수에 인자로 Functional Interface 를 제공하는 방법도 있다.
  
```java
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
```

## Custom Collector Example

위에서 `partitioningBy` 를 이용했던 소수 구하기 알고리즘에 커스텀 컬렉터를 적용해 성능을 개선할 수 있다. 누적된 소수를 검사 대상으로 삼으면 된다. 
스트림 API 의 `filter` 를 사용하면 어떻겠나 하는 생각이 들수도 있겠다. 그러나 `filter` 의 경우 전체 스트림을 처리한 다음 결과를 반환해 비효율적이다. 
   
이 문제를 해결하기 위해 `lazy` 가 아니라 `strict` 버전의 `takeWhile` 를 구현해 보자.

```java
public static boolean isPrime(List<Integer> primes, int candidate) {
    int root = (int) Math.sqrt((double) candidate);

    return takeWhile(primes, i -> i < root)
        .stream()
        .noneMatch(p -> candidate % p == 0);
}
```

그리고 커스텀 컬렉터는 다음과 같이 구현할 수 있다.

```java
public class PrimeNumberCollector
    implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>() {{
            put(true, new ArrayList<Integer>());
            put(false, new ArrayList<Integer>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate))
               .add(candidate);
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        // 병렬로 수행될 리 없으므로 예외를 던짐
        throw new UnsupportedOperationException("invalid combine operation");
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}
```

발견한 소수 순서에 의미가 있으므로 `UNORDERED, CONCURRENT` 옵션은 설정하지 않는다.

```java
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

// output
// using PrimeNumberCollector: 291
// without PrimeNumberCollector: 658
```

