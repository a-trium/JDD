# ordinal 대신 객체 필드를 사용하라

모든 `enum`에는 `ordinal`이라는 메서드가 있는데, `enum`자료형 안에서 `enum`상수의 위치를 나타내는 정수값을 반환한다.

```java
// ordinal을 남용한 사례
public enum Ensemble{
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NOTET, DECTET;

    public int numberOfMusicians() {  return ordinal() + 1;  }
}
```

- 상수의 순서를 변경하는 순간 `numberOfMusicians 메서드`는 깨지게 된다
- 이미 사용한 정수값에 대응되는 새로운 `enum` 상수를 정의할 수 없다
- 새로운 상수는 무조건 이전 상수값보다 1만큼 커야 된다

>원칙은, enum상수에 연계되는 값을 ordinal을 사용해 표현하면 안된다. 필요하다면 객체필드(instance field)에 저장해야 한다.

```java
public enum Ensemble{
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8), NOTET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int numberOfMusicians() {  return this.numberOfMusicians;  }
}
```

자바 `enum` 관련 명세를 보면 `ordinal`에 대한 다음과 같은 설명이 있다.
> "대부분의 프로그래머는 이 메서드를 사용할 일이 없을 것이다. EnumSet이나 EnumMap처럼 일반적인 용도의 enum 기반 자료 구조에서 사용할 목적으로 설계한 메서드이다." 이러한 자료구조를 만들 생각이 없다면, ordinal메서드는 사용하지 않는것이 최선이다.
