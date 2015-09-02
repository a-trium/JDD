# Chapter 10 - Optional

## Null 때문에 발생하는 문제

- 만악의 근원 **NullPointerException**
- `null` 을 확인하기 위해 중첩된 `if` 문을 작성할 경우, 가독성이 떨어진다
- `null` 은 정적 형식 언어에서 값이 없음을 표현하는 방법으로는 적절하지 않다
- `null` 은 자바에서 유일한 포인터다
- `null` 은 무형식이고 정보를 포함하고 있지 않으므로 `null` 이 시스템 전체로 퍼졌을 때 본래의 의미를 파악하기 어렵다

## Groovy 가 Null 을 다루는 방법

그루비는 **safe navigation operator** (`?.`) 을 이용해 `null` 문제를 해결했다. 

```groovy
def carInsuranceName = person?.car?.insurance?.name
```

## Scala, Haskell 이 Null 을 다루는 방법

`Maybe`, `Option` 을 이용해 값이 없음을 하나의 **타입** 으로 다룬다. 자바도 이에 영향을 받아 `Optional` 이란 클래스를 도입했다.

## Optional Basics

```java
public class Insurance {

    public Insurance(String name) { this.name = name; }
    private String name;
    public String getName() { return name; }
}

public class Car {

    public Car() { insurance = Optional.empty(); }
    public Car(Insurance insurance) { this.insurance = Optional.of(insurance); }

    private Optional<Insurance> insurance;
    public Optional<Insurance> getInsurance() { return insurance; }
}

public class Person {
    public Person() { car = Optional.empty(); }
    public Person(Car car) { this.car = Optional.of(car); }

    private Optional<Car> car;
    public Optional<Car> getCar() { return car; }

    public static Optional<String> getInsuranceName(Person p) {
        Optional<Person> optPerson = Optional.of(p);
        return optPerson
            .flatMap(Person::getCar)
            .flatMap(Car::getInsurance)
            .map(Insurance::getName);
    }
}
```

```java
@RunWith(JUnit4.class)
public class Chapter10Spec {

    private Person rich, poor, nobody;
    private final String INSURANCE_NAME = "direct";

    @Before
    public void setUp() {
        Insurance insurance = new Insurance(INSURANCE_NAME);
        Car expensiveCar = new Car(insurance);
        rich = new Person(expensiveCar);
        poor = new Person();
    }

    @Test
    public void test_Optional_empty() {
        Optional<Car> emptyCar = Optional.empty();
        assertEquals(false, emptyCar.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void test_Optional_of_NPE() {
        Optional<Car> optCar = Optional.of(null);
    }

    @Test
    public void test_Optional_ofNullable() {
        Optional<Car> optCar = Optional.ofNullable(null);
        assertEquals(Optional.empty(), optCar);
    }

    @Test
    public void test_Optional_flatMap() {
        assertEquals(Optional.of(INSURANCE_NAME), Person.getInsuranceName(rich));
        assertEquals(Optional.empty(), Person.getInsuranceName(poor));
    }
    
    ...
}
```

<br/>

## 도메인 모델에 Optional 을 사용했을 때 직렬화 할 수 없는 이유

브라이언 고츠가 `Optional` 클래스를 선택형 반환값을 지원하는 곳에만 사용하라고 만들었다.

[SO - Why java.util.Optional is not Serializable](http://stackoverflow.com/questions/24547673/why-java-util-optional-is-not-serializable-how-to-serialize-the-object-with-suc)

- http://mail.openjdk.java.net/pipermail/lambda-libs-spec-experts/2013-May/001814.html
- http://mail.openjdk.java.net/pipermail/lambda-dev/2012-September/005952.html

> Optional has obvious upsides and downsides.  Some of the downsides are: 
- It's a box.  Boxing can be heavy. 
- The more general-purpose value-wrapper classes you have, the more  
some people fear an explosion of unreadable types like 
Map<Optional<List<String>>, List<Optional<Map<String, 
List<Optional<String>>> in API signatures.
  
> I think where we've tried to land is: do things that encourage people to 
use Optional only in return position.  These methods make it more useful 
in return position while not increasing the temptation to use it 
elsewhere any more than we already have.  Hence "mostly harmless".

<br/>

## 옵셔널 값 얻기

다음의 메소드를 사용할 수 있다. 사이드 이펙트를 위해서라면 `forEach` 를 사용하는 편이 낫다. 

- `get`
- `orElse`
- `orElseGet`: Optional 이 없을때만 실행되는 `orElse` 의 게으른 버전이다
- `orElseThrow`
- `ifPresent`

<br/>

## 필터링

```java
    public static String getCarInsuranceName(Optional<Person> optPerson, int minAge) {
        return optPerson
            .filter(p -> p.getAge() >= minAge)
            .flatMap(Person::getCar)
            .flatMap(Car::getInsurance)
            .map(Insurance::getName)
            .orElse("DEFAULT");
    }
```

<br/>

## Practical Example

- `map.get` 을 `Optional` 로 감쌀 수 있다
- `try/catch` 대신 `Optional` 을 이용할 수 있다.

```java
try {
    return Optional.of(Integer.parseInt("asdasd");
} catch (NumberFormatException e) {
    return Optional.empty();
}
```

- `OptionalInt` 등 기본형 `Optional` 을 제공하지만, `Optional` 의 최대 요소 수는 한개이므로 기본형 특화 클래스로 성능을 개선할 수 없다.