# Rule 25 - 배열 대신 리스트를 써라

## 1. 배열은 공변(*variant*) 자료형이고, 제네릭은 불변(*invariant*) 자료형이다. 

```java
Object[] objArray = new Long[1];
objectArray[0] = "I don't fit in"; // ArrayStoreException

List<Object> os = new ArrayList<Long>();
os.add("I don't fit int"); // 컴파일되지 않음
```

## 2. 배열은 실체화(reification) 되는 자료형이다.

즉 배열의 각 원소의 자료형은, 실행시간에 결정된다는 것이다. `String` 객체를 `Long` 배열에 넣으면 `ArrayStoreException` 이 발생한다. 

배열과 다르게, 제네릭은 삭제(*erasure*) 과정을 통해 구현된다. 즉 자료형과 관계된 조건들은 **컴파일 시점에만 적용되고 런타임에 각 원소의 자료형 정보는 삭제된다.**

이런 기본적인 차이 때문에, 배열과 제네릭을 섞어쓰기는 어렵다. `new List<E>[]` 등의 코드는 형 안전성을 보장하지 않으므로 컴파일되지 않는다. 

```java
List<String>[] stringLists = new List<String>[1]; // compile error
List<Integer> intList = Arrays.asList(42);
Object[] objects = stringLists;
objects[0] = intList;

String s = stringLists[0].get(0); // !? ClassCastException
```

> 실체화 불가능(*non-refiable*) 자료형은, 프로그램이 실행될 때 해당 자료형을 표현하는 정보의 양이 컴파일 시점에 필요한 정보의 양보다 적은 자료형을 말한다. `E`, `List[E]`, `List<String>` 등이 그런 예다.

실체화 가능한 형인자 자료형은 `List<?>` 나, `Map<?, ?>` 과 같은 *unbounded wildcard type* 뿐이다. 따라서 비한정적 와일드카드 자료형 배열은 문법상 허용된다. (별로 쓸 일은 없다.)

## 3. 제네릭을 vararg 와 혼용하면 경고 메세지가 많이 출력된다.

`vararg` 메서드를 호출할 때 마다 `vararg` 메서드를 담을 배열이 생성되기 때문이다. 그 배열 원소가 실체화 가능 자료형이 아닐 때에는, 경고 메세지가 출력될 것이다. 
이런 경고는 억제하거나, 제네릭을 `vararg` 와 혼용하지 않도록 주의하는 것 말고는 처리할 방법이 별로 없다.

## 4. 제네릭 배열 생성 오류에 대한 가장 좋은 해결책은 `E[]` 대신 `List[E]` 를 사용하는 것이다.

성능이 저하되거나, 코드가 길어질 수는 있겠으나 형 안전성과 호환성은 좋아진다. 제네릭과 배열을 뒤섞어 쓰다가 컴파일 오류나 경고 메세지를 보게 되면, 
배열을 리스트로 바꿔야겠다는 생각이 본능적으로 들어야 한다.
 
```java
static Object reduce(List list, Function f, Object initVal) {
    synchronized(list) {
        Object result = initVal;
        for (Object o : list) result = f.apply(result, o);
        return result;
    }
}

interface Function {
    Object apply(Object arg1, Object arg2);
}
```

동기화 영역 안에서 *alien method* 를 호출하면 안되므로, *lock* 을 건 상태에서 리스트를 복사한 다음, 복사본에 작업하도록 `reduce` 메소드를 변경하자.

> alien method: A method you call for which you have no control over the code and further don’t even know what the code does, other than the method signature. Most commonly, the term is used for delegate methods, but strictly speaking it could refer to calls to third party libraries. The term was coined by Joshua J. Bloch.


```java
static Object reduce(List list, Function f, Object initVal) {
    synchronized(list) {
        Object snapshot = list.toArray(); // 리스트 내부적으로 락을 건다.
        Object result = initVal;
        for (Object o : snapshot) result = f.apply(result, o);
        return result;
    }
}

interface Function<T> {
    T apply(T arg1, T arg2);
}
```

```java
static Object reduce(List list, Function f, Object initVal) {
    synchronized(list) {
        E[] snapshot = (E[]) list.toArray(); // 리스트 내부적으로 락을 건다.
        Object result = initVal;
        for (Object o : snapshot) result = f.apply(result, o);
        return result;
    }
}

interface Function<T> {
    T apply(T arg1, T arg2);
}
```

제대로 동작하긴 하지만, 안전하진 않다. 컴파일 시점의 자료형은 `E[]` 인데, `Integer[]` 등이 될 수 있다. 런타임에는 `Object[]` 이므로 위험하다.

따라서 리스트를 쓰자. 배열 대신!


```java
static Object reduce(List list, Function f, Object initVal) {
    synchronized(list) {
        List<E> snapshot;
        synchronized(list) {
            snapshot = new ArrayList<E>(list); 
        }
        
        E result = initVal;
        
        for (E e : snapshot)
          result = f.apply(result, e);
        
        return result;
    }
}

interface Function<T> {
    T apply(T arg1, T arg2);
}
```


