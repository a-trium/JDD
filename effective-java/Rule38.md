# Rule 38 - 인자의 유효성을 검사하라

## Public 메서드의 경우 주석에 인자를 잘못 넣으면 어떤 예외가 발생할 수 있는지 적어라

```java
/**
  * @param ...
  * @return ...
  * @throws ArithmeticException (m <= 0 일 때)
  */
  
public BigInteger mod(BigInteger m) { ... }  
```

일반적으로 사용되는 예외는 다음과 같다

- `NullPointerException`
- `IllegalArgumentException`
- `IndexOutOfBoundException`

## Private 메서드의 경우 인자를 통제할 수 있으므로 Assert 를 이용한다

```java
private void sort(long a[], int offset, int length) {
    assert a != null
    assert offset >= 0 && offset <= a.length
    ...
    ...
}
```

실행되지 않은 `Assert` 는 비용이 0 이다. (Assert 를 활성화 시키려면 `-enableassertions` 옵션이 필요)

## 컬렉션 등 나중을 위해 보관되는 원소는 특히 더 유효성을 검사해야한다.

<br/>

## 생성자에서는 클래스 불변식(invariant)를 위반하는 객체가 만들어지는지 반드시 검사해야 한다.

## 유효성 검사를 하지 않아도 되는 경우

- 오버헤드가 크거나 
- 계산 과정에서 유효성 검사가 자동으로 일어나는 경우 (e.g 정렬 계산시, 모든 원소는 비교 가능함을 알 수 있음. 아닐 경우 `ClassCastException` 발생)

그러나 이런 암묵적인 유효성 검사에 지나치게 기대다 보면 **실패 원자성(failure atomicity)** 를 잃어버린다. 그리고, 만약 계산 과정 도중에 이런 암묵적인 검사에 예외가 발생한다면, 주석에 명시된 예외로 감싸서 던져야 한다.


