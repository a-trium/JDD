# Chapter 4

## Basic Stream Examples

```java
public class Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public int getCalories() {
        return calories;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type { MEAT, FISH, OTHER }
}

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
```

## Traversable Only Once 

```java
@Test(expected = IllegalStateException.class)
public void test_cannot_reuse_stream() {
    Stream<String> ss = Arrays.asList("Java8", "In", "Action").stream();
    ss.forEach(System.out::println);
    ss.forEach(System.out::println);
}
```

## External vs. Internal Iteration

`for-each` 같은 외부 반복에서는 병렬성을 스스로 관리해야 한다. 자바 8에서는 컬렉션 인터페이스와 비슷하면서도 반복자가 없는 (내부반복) 스트림을 이용 병렬성을 명시적으로 관리하지 않고, 코딩할 수 있다. 

## Intermediate Operations

다음의 중간 연산은 파이프라인만 구성하고, 실제 연산은 수행하지 않는다.

- filter 
- map
- limit
- sorted
- distinct

## Terminal Operations

다음의 최종 연산은 파이프라인을 실행한다.

- forEach
- count
- collect


