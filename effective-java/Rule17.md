# Rule 17 - 계승을 위한 설계와 문서를 갖추거나, 그럴 수 없다면 상속을 금지해라

## 1. 메서드를 재정의하면 무슨일이 생기는지 정확하게 문서로 남겨야 한다

**재정의 가능 메서드를 내부적으로 어떻게 사용하는지 반드시 남기라는 것이다.** 

- `public` 이나 `protected` 로 선언된 모든 메서드와 생성자에 대해 어떤 재정의 가능 메서드를 어떤 순서로 호출하는지
- 호출 결과가 추후 어떤 영향을 미치는지 문서로 남겨야한다.

[AbstractCollection.remove](http://docs.oracle.com/javase/7/docs/api/java/util/AbstractCollection.html#remove(java.lang.Object))

> This implementation iterates over the collection looking for the specified element. If it finds the element, it removes the element from the collection using the iterator's remove method.

이 문서를 보면 `iterator` 메서드를 재정의하면 `remove` 가 영향 받는다는 사실을 알 수 있다. (`HashSet` 에서는 `add` 를 재정의하면 `addAll` 에 무슨 일이 생기는지 알 수 없었다.)

## 2. 클래스 내부 동작에 개입할 수 있는 훅을 신중하게 골라, `protected` 메소드로 제공하는 방법도 있다

```java
protected void removeRange(int fromIndex, int toIndex)
```

이 메서드를 재정의해서 리스트 구현체의 내부 구현을 활용하면 `clear` 성능을 꽤 높일 수 있다. 다만 `protected` 훅 메소드를 제공하기 전에, 
반드시 하위 클래스를 만들어서 테스트해 보아야 한다. 

## 3. **중요** 계승을 허용하려면 다음의 제약사항을 반드시 따라야 한다

(1) **생성자는 직접적이건 간접적이건 재정의 가능 메서드를 호출하면 안된다.**
 
(2) `Cloneable` 과 `Serializable` 를 구현하는것은 일반적으로 바람직하지 않은데, 계승하는 클래스를 구현할 프로그래머에게 
과도한 책임을 지우기 때문이다. (선택적으로 구현할 수 있도록 하는 방법들이 있다. Rule 11, 74)

계승을 위해 설계하는 클래스에서 `Cloneable` 이나 `Serializable` 을 구현하기로 결정했다면 `clone` 이나 `readObject` 메서드도 생성자와 비슷하게 동작하기 때문에 
`clone` 이나 `readObject` 메서드 안에서 직접적이건, 간접적이건 **재정의 메서드를 호출하지 않도록 해야 한다.**

- `readObject` 메서드 안에서 재정의 가능 메서드를 호출하게 되면, 하위 클래스의 상태가 완전히 **deserialize** 되기 전에 해당 메서드가 실행 된다.
- `clone` 의 경우에는 하위 클래스의 `clone` 메서드가 복사본 객체의 상태를 미처 수정하기도 전에, 해당 메서드가 실행되어 버릴 것이다. 
이 오류는 복사본뿐 아니라 원래 객체까지도 망가뜨린다. 예를 들어 재정의 된 메서드가 `clone` 이 만들어낸 복사본의 내부 구조 깊은 곳까지 건드릴 경우를 생각해보면, 
복사본은 채 완성되지 않은 상태임을 알 수 있다.

(3) `Serializable` 을 구현하는 계승 클래스에서 `readResolve` 와 `writeReplace` 메서드가 있다면, 반드시 `protected` 로 선언해야 한다. (private 가 아니라) 
`private` 로 선언해 버리면 하위 클래스는 해당 메서드를 조용히 무시한다. 

## 4. 계승에 맞도록 설계하고 문서화하지 않은 클래스에 대한 하위 클래스는 만들지 말자

하위 클래스 생성을 금지하기 위한 방법에는 두 가지가 있는데,
 
- 클래스를 `final` 로 선언
- 모든 생성자를 `private` 이나 `package-private` 로 선언하고 생성자 대신 `public` 정적 팩토리를 추가

일반적으로 기능 추가를 위해 일반 클래스를 상속해서 사용하는데, 이 경우 계승 대신 **wrapper class** 를 사용하는 것이 낫다. 만약 계승을 반드시 
허용해야 한다면, 재정의 가능 메서드는 절대로 호출하지 않도록 하고, 그 사실을 반드시 문서에 남겨야 한다.

재정의 가능 메서드를 기계적으로 제거하는 방법은, 재정의 가능 메서드의 내부 코드를 `private` 로 선언된 헬퍼 메소드로 옮기고, 
각각의 재정의 가능 메서드가 헬퍼 메서드를 호출하게 하면 된다. 




