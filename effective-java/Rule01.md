# 1. 생성자 대신 정적 팩터리 메서드를 사용할 수 없는지 생각해 보라

## 장점

`public` 생성자 대신 정적 팩터리 메서드를 제공하면 다음과 같은 장점이 있다.

1. 생성자와는 달리 팩토리 메소드는 함수 이름을 가질 수 있다.

소수를 생성할 가능성이 높은 `BigInteger(int, Random)` 의 경우 `BigInteger.probablePrime` 이면 더 이해하기 쉬웠을 것이다.

또한 파라미터 순서만 바꾼 생성자를 제공할 경우 오해할 여지가 있다.

2. 호출하기 위해 새로운 객체를 생성할 필요가 없다.

생성자와는 달리 캐시할 수 있다. `Boolean.valueOf(Boolean)` 이 좋은 예다. 즉, 어떤 시점에 어떤 객체가 얼마나 존재할지 정밀하게 제어할 수 있다. 
싱글톤처럼, 한개의 객체만 존재하게 만든다면 `equals` 대신 `==` 로 성능을 향상시킬 수 있다.

3. 생성자와는 달리 return 자료형의 하위 형식(subclass) 도 반환이 가능하다.

심지어 반환되는 클래스가 **public** 이 아니어도 된다.

JDK 1.5 에 추가된 `java.utilEnumSet` 의 경우 `public` 생성자가 없고 정적 팩토리 메서드 뿐인데, 이 메서드들은 `enum` 의 상수에 따라서 다른 구현체를 돌려준다.

- 64개보다 많을 경우 `JumboEnumSet` 
- 64개 이하일 경우 `RegularEnumSet`

4. Parameterized Type (Generic) 객체를 만들때 편리하다.

```java
Map<String List<String>> m = new HashMap<String, List<String>>();

// 대신
public static HashMap<K, V> newInstance() {
  return new HashMap<K, V>();
}

Map<String List<String>> m = newInstance();
```

## 단점

1. `public`, `protected` 생성자가 없으므로 하위 클래스를 만들 수 없다.

2. 정적 팩토리 메소드가 다른 정적 메소드가 확연히 구별되지 않는다.

일반적으로 다음과 같은 이름을 사용한다.

- `valueOf`: 인자로 주어진 값과 같은 값을 갖는 객체를 반환 (e.g `Boolean.valueOf(false)`)
- `of`: valueOf 를 더 간단하게 작성한 것
- `getInstance`: 인자와 같은 값을 갖지 않을 경우. 싱글톤일 경우 같은 객체만 돌려줌.
- `newInstance`: 호출할 때 마다 항상 다른 객체
- `getType`: 팩토리 메서드가 다른 클래스에 있을 경우
- `newType`: 팩토리 메서드가 다른 클래스에 있을 경우






