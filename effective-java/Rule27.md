# 규칙27. 가능하면 제네릭 메서드로 만들 것

`예제`
```java
public static Set union(Set s1, Set s2){
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
```

메서드를 제네릭화 하는 방법
1. 보관될 원소의 자료형을 나타내는 `형인자(type parameter)`를 메서드 선언에 추가
2. 위에 선언한 형인자를 사용해 메서드를 구현
3. **형인자를 선언하는 형인자 목록(type parameter list)은 메서드의 수정자(modifier)와 자료형 사이에 둔다**

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2){
    Set<E> result = new HashSet<E>(s1);
    result.addAll(s2);
    return result;
}
```

컴파일러가 메서드에 전해진 인자의 자료형을 보고 `자료형 유추(type inference)`하여 형인자값을 알아낸다. 때문에 명시적으로 주어야 했던 형인자를 전달할 필요가 없다.

## 제네릭 싱글턴 패턴(generic singleton pattern)
제네릭은 자료형 삭제(erasure)과정을 통해 구현되므로 모든 필요한 형인자화(type parameterization) 과정에 동일 객체를 활용할 수 있는데, 형인자와 과정마다 같은 객체를 나눠주는 정적 팩터리 메서드를 작성해야한다.

`예제` T형의 값을 받고 반환하는 함수 인터페이스
```java
public interface UnaryFunction<T>{
    T apply(T arg);
}
```

위 항등함수는 무상태(stateless)함수 이므로, 새로운 함수를 만드는것은 낭비  
실체화되지 않는 자료형 이므로, 컴파일 시점에 자료형이 삭제된다. 때문에 이를 이용하면 제네릭 싱글턴 하나만 가지고 구현이 가능하다.

```java
// 제네릭 싱글턴 팩터리 패턴
private static UnaryFunction<Object> IDENTITY_FUNCTION = new UnaryFunction<Object>(){
    public Object apply(Object arg){
        return arg;
    }
}

// IDENTITY_FUNCTION은 무상태 객체고 형인자는 비한정 인자이므로(unbounded)
// 모든 자료형이 같은 객체를 공유해도 안전
@SuppressWarnings("unchkeced")
public static <T> UnaryFunction<T> identityFunction(){
    return (UnaryFunction<T>) IDENTITY_FUNCTION;  // 형변환시 무점검 형변환(unchkeced cast)경고 발생
}
```

상대적으로 사용 빈도가 낮긴하나, 형인자가 포함된 표현식으로 형인자를 한정하는 것도 가능하다. `재귀적 자료형 한정(recursive type bound)`  
재귀적 자료형 한정은 자료형의 자연적 순서를 정의하는 `Comparable` 인터페이스와 함꼐 가장 흔히 쓰인다.

```java
public interface Comparable<T>{
    int comparaeTo(T o);
}
```

`Comparable`을 구현하는 메서드는 리스트의 각 요소들이 비교 가능해야한다. 이런 조건을 다음과 같이 표현한다.

```java
public static <T extends Comparable<T>> T max(List<T> list){
    ... // 생략
}
```

자료형 한정 `<T extends Comparable<T>>`는 **자기 자신과 비교 가능한 모든 자료형 T** 라는 뜻으로 읽을 수 있다.  
> 재귀적 자료형 한정은 이보다 복잡하게 쓰일 수 있으나, 흔한일은 아니다!


## 요약
제네릭 메서드는 클라이언트가 직접 입력 값과 반환값의 자료형을 형변환해야 하는 메서드보다 사용하기 쉽고 형 안전성도 높다.  
새로운 메서드를 고안할 때, 형 변환없이도 사용할 수 있느니 확인해보면 제네릭으로 만드는 것이 좋겠다는 판단을 내리게 될 때가 많을 것이다.
