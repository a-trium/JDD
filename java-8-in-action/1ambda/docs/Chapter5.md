# Chapter 5

## Searching, Matching

검색과 매칭을 위해 다음의 메소드를 이용할 수 있다. 위 연산들은 모두 쇼트서킷 평가를 수행한다. 

- allMatch
- anyMatch
- noneMatch
- findFirst
- findAny

```java
menu.stream()
    .filter(Dish::isVegetarian)
    .findAny()
    .ifPresent(d -> System.out.println(d.getName()));
```

`findFirst` 는 병렬성에 제약을 주기 때문에 일반적으로 `findAny` 를 사용한다.

## Reducing

```java
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
```

## Bounded, Unbounded Intermediate Operations

`map`, `filter` 등은 연산을 수행하는 과정에서 중간값이 필요 없지만, `reduce`, `limit`, `distinct`, `sorted` 등의 연산은 상태를 저장할 중간값이 필요하다.
 
- `reduce`, `limit` 과 같은 연산은 **상태** 가 필요하긴 하지만 그 크기가 한정되어 있다. (**bounded**)
- `sorted`, `distinct` 연산은 **상태** 가 필요하고, 그 크기가 한정되어 있지 않다. (**unbounded**)

## Transaction Example

```java
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
```

## Number Stream

```java
int calories = menu.stream()
                .map(Dish::getCalories)
                .reduce(0, Integer::sum);
```

위 코드는 내부적으로 합계를 계산하기 전에 `Integer` 를 기본형으로 언박싱해야 한다. 직접 `sum` 을 호출하려면, 기본형 특화 스트림을 사용해야 한다. 

```java
int calories = menu
    .stream()
    .mapToInt(Dish::getCalories)
    .sum();

assertEquals(4200, calories);

// 객체 스트림 복원 
IntStream is = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> si = is.boxed();
```

## OptionalInt

```java
OptionalInt maxCalories = new ArrayList<Dish>()
    .stream()
    .mapToInt(Dish::getCalories)
    .max();

int max = maxCalories.orElse(1);
assertEquals(1, max);
```

## Stream Examples

```java
@Test
public void test_pythgoreanTriples() {
    Stream<int []> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
        .flatMap(a ->
            IntStream.rangeClosed(1, 100)
                     .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
                     .filter(t -> t[2] % 1 == 0)
        );

    pythagoreanTriples.limit(5)
        .forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
}
```

## Generate Stream

```java
@Test
public void test_generate_stream() {
    Stream<String> s1 = Stream.of("1", "2");
    Stream<String> s2 = Stream.empty();
    int max = Arrays.stream(new int[]{1 ,2, 3, 4}).max().orElse(1);
}
```

NIO API 에서도 Stream 을 활용할 수 있다. 

```java
long uniqCount = 0L;

try(Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
    uniqCount = lines.flatMap(line -> Arrays.stream(line.split(" "))
                      .distinct()
                      .count();

} catch (IOException e) {}
```

스트림은 자원을 자동으로 해제할 수 있는 `AutoClosable` 이다.

## Iterate, Generate

```java
@Test
public void test_iterate() {
    long count = Stream.iterate(0, n -> n + 2)
                       .limit(10)
                       .count();


    assertEquals(10, count);

    Stream.iterate(0, n -> n + 2)
          .limit(2)
          .forEach(System.out::println);
}

@Test
public void test_fibo() {
    Stream<Integer> fiboGen = Stream
        .iterate(new int[]{0, 1}, t -> new int[] {t[1], t[0] + t[1]})
        .limit(5)
        .map(t -> t[0]);

    List<Integer> fibo = fiboGen.collect(toList());

    assertEquals(Arrays.asList(0, 1, 1, 2, 3), fibo);
}

@Test
public void test_generate() {
    Stream.generate(Math::random)
        .limit(5)
        .forEach(System.out::println);
}
```

`generate` 는 `Supplier<T>` 를 인수로 받아 새로운 값을 생성한다. 만약 `1` 을 생성하는 무한 스트림을 생성하고 싶다면, 다음처럼 작성하면 된다. 

```java
IntStream ones = IntStream.generate(() -> 1);
```

