# Rule 15 - 변경 가능성을 최소화 하라


자바의 `String`, `BigInteger`, `BigDecimal` 가 대표적인 **immutable class** 다. **immutable class** 를 만들때는 다음의 다섯 규칙을 따르면 된다.

1. 객체 상태를 변경하는 메서드(setter, mutator) 를 제공하지 않는다.

2. 상속 불가능하도록 `final` 키워드를 이용한다.

3. 모든 필드를 `final` 로 선언한다.

이렇게 하면, 자바 메모리 모델에 명시된 바와 같이 새로 생성된 객체에 대한 참조가 동기화 없이 다른 스레드로 전달 되어도 안전하다.

4. 모든 필드를 `private` 로 선언한다.

5. 변경 가능 컴포넌트에 대한 독점적 접근권을 보장한다.

클래스에 포함된 변경 가능 객체에 대한 참조를 클라이언트가 획득할 수 없어야 한다. 그런 필드는 클라이언트가 제공하는 객체로 초기화 해서는 안되고, 
accessor 또한 그런 필드를 반환해서도 안된다. **따라서 생성자, 접근자, `readObject` 메서드 안에서는 방어적 복사본을 만들어야 한다.**

<br>

**immutable object** 는 스레드에 안전할 수 밖에 없다. (!!) 어떤 동기화도 필요 없으며, 스레드 안전성을 보장하는 가장 쉬운 방법이다.


```java
// 정적 팩토리 메소드 패턴(Rule 1) 을 이용하여 개선할 수 있음
public static final Complex ZERO = new Complex(0, 0)
``` 

- 변경 불가능한 객체를 자유롭게 공유할 수 있다는 것은, **방어적 복사본** 을 만들 필요가 없다는 뜻이다. 필요가 없는데, 만들어 봐야 원래 객체와 같이 때문이다. 
따라서 변경 불가능 클래스에 대해서도 `clone` 메소드나 복사 생성자는 만들 필요도 없고, 만들어서도 안된다. `String` 에는 실수로 만들어진 복사 생성자가 있는데, 사용을 피해야 한다. 

- 변경 불가능한 객체는 내부도 공유할 수 있다. 예를 들어 `BigInteger` 클래스는 값의 부호와 크기를 `int` 변수와 배열로 표현하는데, `negate` 메소드는 같은 크기의 값을 부호만 바꿔서 새로운 
`BigInteger` 로 반환하고, 배열은 변경하지 않는다. 

- 변경 불가능한 객체는 다른 객체의 구성요소로도 훌륭하다. `Map` 의 키나 `Set` 의 원소로 사용하기 좋은데, 한번 집어넣고 나면 그 값이 변경되지 않으니 불변식(invariant) 가 깨질 걱정을 하지 않아도 된다.

- 변경 불가능한 객체의 단점은, 값마다 별도의 객체를 만들어야 한다는 점이다.

`BigInteger` 에서 딱 1비트만 변경하는 경우에 `BigInteger` 클래스를 다시 만들어야 한다. `BitSet` 클래스는 변경 가능 클래스기 때문에 이런 문제가 없다. 이런 문제를 해결하기 위해서는

1. 다단계 연산 가운데 자주 요구되는것을 **primitive** 연산으로 제공하면, 연산의 중간 과정에서 별도로 객체를 만들 필요가 없다. `BigInteger` 의 경우 
*package-private companion class* 를 이용해서 연산의 속도를 높인다. 

2. 클라이언트가 어떤 다단계 연산을 적용할지 정확하게 예측할 수 없다면, **변경 가능한 public companion class** 를 제공하는것이 최선이다. 
예를 들어 `String` 의 변경 가능한 공용 동료 클래스는 `StringBuilder` 가 있다. (그리고 거의 지원이 중단된 `StringBuffer`)
 
<br/>

## 변경 불가능 클래스 구현 방법

보통 `final` 을 이용하면 되지만, 그 보다 더 유연한 방법도 있다. 모든 생성자를 `private` 로 선언하고, `public` 생성자 대신 `public` 정적 팩토리를 제공하면 된다.

```java
class Complex {
  private Complex(double re, double im) {
    this.re = re;
    this.im = im;
  }
  
  public static Complex valueOf(double re, double im) {
    return new Complex(re, im);
  }
  
  ...
}
```

이렇게 만들면 상속이 불가능한데, 해당 패키지 외부에서 사용할 `public`, `protected` 생성자가 없기 때문이다.

만약 상속이 불가능하도록 만들지 않으면, 인자의 변경 불가능성에 보안이 좌우되는 클래스를 작성할 경우에는 전달된 인자가 `BigInteger` 의 하위 클래스가 아니라, 
진짜 `BigInteger` 인지 검사해야 한다. 만약 하위 클래스라면 변경 가능한 객체일지도 모른다는 가정하에 **방어적 복사** 를 해야한다.
 
```java
public static BigInteger safeInstance(BIgInteger val) {
  if (val.getClass() != BigInteger.class)
    return new BigInteger(val.toByteArray());
    
  return val;
}
```

모든 필드가 **final** 이여야만 하는것은 아니다. **non-final** 필드를 계산 결과를 캐싱할 수 있다. 이것이 가능한 이유는 객체가 **immutable** 이기 때문.

<br/>

## 직렬화

한가지 더 주의할 부분은 **직렬화** 에 관계된 부분이다. 변경 불가능 클래스가 `Serializable` 인터페이스를 구현했고, 해당 클래스에서 
변경 가능 객체를 참조하는 필드가 있다면, 

- `readObject` 메소드나 `readResolver` 메서드를 반드시 제공해야 한다. 
- 아니면 `ObjectOutputStream.writeUnshared` 나 `ObjectInputStream.readUnshared` 메서드를 반드시 사용해야 한다.

그렇지 않으면 공격자가 변경 불가능 클래스로 부터 변경 가능 객체를 만들 수 있다.

<br/>

## 요약

- 변경 가능한 클래스로 만들어야할 이유가 없다면, 반드시 변경 불가능 클래스로 만들자. 모든 필드에 Setter 가 있을 이유는 없다.

- 변경 불가능하게 만들 수 없다면, 가능성을 최대한 제한하라. 객체가 가질 수 있는 상태가 줄면, 객체의 상태를 추론하기 쉬워지고, 오류 가능성도 줄어든다. 

- **특별한 이유가 없다면 모든 필드는 final** 로 선언하라

- 특별한 이유가 없다면 생성자 이외의 초기화, 재 초기화 메서드를 제공하지 마라.

`TimerTask` 가 이 원칙을 잘 보여주는 사례인데, 변경 가능한 클래스긴 하지만 가질 수 있는 상태의 범위는 적다. 원할 때 실행시킬 수 있으나, 완전히 실행되거나, 취소된 이후에는 
다시 실행히킬 수 없다.