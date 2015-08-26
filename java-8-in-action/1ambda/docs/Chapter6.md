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

```
