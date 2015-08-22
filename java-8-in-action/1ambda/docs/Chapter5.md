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

