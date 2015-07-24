# 규칙28. 한정적 와일드카드를 써서 API 유연성을 높여라

`형인자 자료형(parameterized type)`은 `불변(invariant)` 자료형이다. 다시말하면 `List<Type1>`과 `List<type2>`사이에 어떤 상하위 관계도 성립할 수 없다.

### pushAll 예제
```java
public void pushAll(Iterable<E> src){
    for (E e : src)
      push(e);
}
```

위 메서드에서 `Stack<Number>`인경우를 생각해보자. `Integer`형의 `intVal`로 `push(intVal)`을 호출하면 제대로 동작할 것이다. `Integer`는 `Number`의 하위 자료형이기 때문이다. 그러니 다음코드도 문제가 없어야한다.

```java
Stack<Number> numberStack = new Stack<Number>();
Iterable<Integer> integers = ...;
numberStack.pushAll(integers);
```

하지만 형인자 자료형이 ***불변*** 이기 때문에 에러가 발생한다. 자바에서 이를 해결하기위하여 `한정적 와일드 카드 자료형(bounded wildcard type)`라는 특별한 형인자 자료형을 제공한다.

```java
public void pushAll(Iterable<? extends E> src){
    for (E e : src)
      push(e);
}
```

이렇게 바꾸면 컴파일 뿐만아니라, 원래 `pushAll`메서드로 컴파일 되지 않았던 코드까지 컴파일된다. 왜냐하면 `한정적 와일드 카드 자료형`을 통해 `pushAll`메서드의 인자 자료형을 **E의 Iterable** 이 아니라 ***E의 하위 자료형의 Iterable*** 이라고 명시했기 때문이다.
> 규칙 26에서 설명한 "모든 자료형은 자기 자신의 하위자료형"을 떠올려 보자


### popAll 예제
```java
public void popAll(Collection<E> dst){
    while (!isEmpty())
      dst.add(pop());
}
```

마찬가지로 위 메서드에서 `Stack<Number>`, `Collection<Object>` 라고 하자. 마찬가지로 아래 소스도 논리적으로 가능해야 한다.

```java
Stack<number> numberStack = new Stack<Number>();
Collection<Object> object = ...;
numberStack.popAll(object);
```

하지만, 위와 마찬가지로 ***하위 자료형이 아니라 오류*** 가 발생한다. 이때는 `popAll`의 인자 자료형을 `E의 컬렉션`이 아니라 `E의 상위 자료형의 컬렉션`이라고 명시를 해야한다.

```java
public void popAll(Collection<? super E> dst){
    while (!isEmpty())
      dst.add(pop());
}
```

이를 통해서..
 >유연성을 최대하하려면, 객체 생산자(producer)나 소비자(consumer)구실을 하는 메서드 인자의 자료형은 와일드 카드 자료형으로 해야 한다.

와일카드 구분 `Tip!`
`PECS (Produce - Extends, Consumer - Super)`  
- 인자가 T 생산자라면 `<? extends T>`
- 인자가 T 소비자라면 `<? super T>`


### 반환값에는 와일드카드 자료형을 쓰면 안된다.
1. 클라이언트 코드에서 와일드카드 자료형을 명시해야 한다.
2. 클라이언트 코드에서 와일드 카드 자료형에 대한 고민을 한다면, 아마 클래스 API가 잘못 설계된 것이다.


```java
// swap 메서드를 선언하는 두가지 방법
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

메서드 가운데 상당수는 두가지 선언방식을 모두 사용할 수 있다. 이중 두번째 방법이 일반적으로 좀더 바람직하다. 이유는 **간단하기 때문** 이다.  
형인자 메서드 선언에 단 한군데 나타난다면 해당 인자를 와일드카드로 바꾸는것이 원칙이다.
그런데 아래 코드가 컴파일되지 않는다.

```java
public static void swap(List<?> list, int i, int j){
    list.set(i, list.set(j, list.get(i)));
}
```

위 코드의 문제는 `list` 자료형이 `List<?>`이기 때문이다. `List<?>`에는 `null`이외에 어떤값도 넣을 수 있다. 때문에 `private` 헬퍼 메서드를 이용해 와일카드 자료형을 `포착(capture)`하면 된다.

```java
public static void swap(List<?> list, int i, int j){
    swapHelper(list, i, j);
}

public static <E> void swapHelper(List<E> list, int i, int j){
    list.set(i, list.set(j, list.get(i)));
}
```

`swapHelper`에서는 list가 `List<E>`라는 것이 명시되어 있기때문에, 컴파일 타임에 타입을 유추할 수 있다.  
요약하자면 ***API는 와일드카드 자료형을 사용하여 유연성을 높이자*** 라는 것이다. 
