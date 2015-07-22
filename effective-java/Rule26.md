# 규칙26. 가능하면 제네릭 자료형으로 만들 것

클래스를 제네릭화 하는 방법
1. `선언부에 형인자(type parameter)`를 `E`를 추가한다. (`E`는 관습적 명칭)
2. 해당 자료형을 사용하는 모든 부분을 형인자 `E`로 대체하고 컴파일한다.

**예제**
```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack(){
        elements = new E[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e){
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop(){
        if(size == 0)
          throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null;            // 참조제거
        return result;
    }
    ... // 생략
}
```
위 예제를 컴파일하면 하나 이상의 오류나 경고메시지를 만나게된다.
> Stack.java:8: generic array creation  
> elements = new E[DEFAULT_INITIAL_CAPACITY];

`E`와 같은 실체화 불가능 자료형으로 배열을 생성할 수 없기 때문에 다음과 같은 두가지 방법으로 해결한다.

- Object배열을 만들어서 제네릭 배열 자료형으로 cast하는 방법
```java
@SuppressWarnings("unchecked")
public Stack(){
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];  
}
```
컴파일러가 프로그램의 형 안전성을 입증할 수 없으므로, 프로그래머가 형 안전성을 해치지 않음을 확실하게 해야한다.(명시적 형 변환이나 ClassCastException이 발생하지 않도록)
- elements의 자료형을 E[]에서 Object[]로 바꾸는 방법
```java
public class Stack<E> {
  private Object[] elements;

  ... // 생략

  public E pop(){
      if(size == 0)
        throw new EmptyStackException();
      @SuppressWarnings("unchecked")
      E result = (E)elements[--size];
      elements[size] = null;            // 참조제거
      return result;
  }
  ... // 생략
```
단, 위와같이 바꿨을때 elements에서 꺼내는 모든 원소는 `Object`에서 `E`로 바꿔야하며, 첫번째 방법과 같이 프로그래머가 확인을 한 후 경고를 억제하면 된다.

>모든 조건이 같다면, 무점검 형변환(unchecked cast) 경고 억제의 위험성은 스칼라 자료형보다 배열 자료형이 크기 때문에 두번째 해법이 낫다고 볼 수 있지만, 현실적인 클래스에서 elements를 사용하는 코드가 클래스 이곳저곳 흩어져 있으므로, 첫번째 방법을 적용하면 E[] 한번만 변환하면 되므로 더 보편적으로 쓰인다.

규칙25번의 배열 대신 리스트를 사용하라와 모순되는것 처럼 보이지만, 리스트는 자바의 `native type`이 아니므로 어떤 제네릭 자료형도 반드시 **배열** 위에 구현되어야 한다.  
하지만, `primitive type`은 자바 제네릭 시스템의 문제상 리스트에 사용할 수 없으므로 `객체화된 기본 자료형(boxed primitive type)`을 사용해야 한다.


## 형인자를 제한하는 제네릭 자료형
인자목록에 `<E extends Delayed>`와 같이 적으면, 실 형 인자 `E`는 반드시 `Delayed`의 하위 자료형이여야 한다. 이런 `한정적 형인자(bounded type parameter)`를 사용하면 자료형을 형변환하거나 ClassCastException이 발생하지 않으므로, 안전할 뿐 아니라 사용하기도 쉽다.
