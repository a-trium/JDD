# Chapter 8 - Default Method

자바 8에서는 기본 구현을 포함하는 인터페이스를 정의할 수 있다

- 인터페이스 내부에 **정적 메소드** 를 구현하거나
- 인터페이스 내부에 **디폴트 메서드** 를 구현하는 것이다.

```java
public interface List<E> extends Collection<E> {
    
    ...
    ...
    
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }
    
    ...
    ...
}
```

디폹 메서드를 이용하면 인터페이스의 기본 구현을 그대로 가져가면서 인터페이스에 자유롭게 새로운 메서드를 추가할 수 있다.

<br/>

## Resizable Example
 
다음과 같은 `Resizable` 인터페이스가 있다 하자.

```java
public interface Resizable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);
    void setRelativeSize(int width, int height); // 새로 추가된 메소드
}
```

새로운 버전에서 `setRelativeSize` 함수가 추가되었을 경우 **바이너리 호환성** 은 유지된다. 즉 새로 추가된 메소드를 호출하지 않으면 
`Resizable` 구현 클래스에서 `setRelativeSize` 메서드 구현이 없이도 기존 클래스 파일이 잘 동작한다.

만약 누군가가 `Resizable` 객체를 받아 `setRelativeSize` 를 호출하는 함수에 `setRelativeSize` 구현이 없는 클래스를 넘긴다면 `AbstractMethodError` 가 
발생할 것이다.

또한 인터페이스의 추상 메소드 변경시 소스코드 호환성은 보장되지 않기 때문에, `Resizable.setRelativeSize` 구현을 추가하지 않고서는 컴파일이 불가능하다.

<br/>

## Compatibility

자바 프로그램 변경시 발생하는 호환성 문제는 크게 **바이너리 호환성**, **소스 호환성**, **동작 호환성** 으로 나눌 수 있다.

- 변경 이후에도 기존 바이너리가 실행될 수 있는 상황이면 **바이너리 호환성** 이 지켜진다고 말한다. (바이너리 실행에는 verification, preparation, resolution 등의 과정이 포함된다.) 

- **소스 호환성** 이란 코드를 고쳐도 기존 프로그램을 성공적으로 재컴파일할 수 있음을 의미한다. 

- **동작 호환성** 이란 코드를 바꾼 다음에도 같은 입력값에 대해 프로그램이 같은 동작을 보임을 말한다.

<br/>

## Default Method

자바 8에서는 `Collection` 인터페이스에 `removeIf` 를 바이너리 호환성을 유지하면서 추가하기 위해 `default` 메서드로 구현했다.

```java
public interface Collection<E> extends Iterable<E> {

    ...
    ...
    
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
    
    ...
    ...
}
```

디폴트 메서드 활용 패턴은 크게 2 가지로 나눌 수 있다.

- **선택형 메서드**
- **동작 다중 상속**

### Optional Methods

```java
interface Iterator<T> {
    boolean hasNext();
    T next();
    default void remove() {
        throw new UnsupportedOperationException();
    }
}
```

디폴트 메서드를 이용하면 `remove` 같은 메서드에 기본 구현을 제공할 수 있으므로 인터페이스를 구현하는 클래스에서 
빈 구현을 제공할 필요가 없다. 불필요한 코드를 없앨 수 있으나, 사용자 입장에서는 인터페이스 구현시 
디폴트 메소드가 예외를 던지는지, 아닌지를 확인해야 한다는 추가적인 단점이 있다. 따라서 최소한의 메소드로만 
인터페이스를 구성할 필요가 있다.

### Multiple Inheritance of Behavior

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
         
         ...
         ...
```

`ArrayList` 는 동작의 상속을 위해 여러개의 인터페이스를 구현하나, 이들 동작을 직접 구현해야 하는 단점이 있었다. 
`default` 인터페이스를 이용하면 이 문제를 해결할 수 있다.

<br/>

```java


public interface Resizable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);
    
    default void reRelativeSize(int wFactor, int hFactor) {
        setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
    }
}

public interface Rotatable {
    void setRotationAngle(int angleInDegrees);

    int getRotationAngle();

    default void rotateBy(int angle) {
        setRotationAngle((getRotationAngle() + angle) % 360);
    }
}

public interface Movable {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);

    default void moveHorizontally(int distance) {
        setX(getX() + distance);
    }

    default void moveVertically(int distance) {
        setY(getY() + distance);
    }
}
```

이제 기본 동작이 포함된 `Monster`, `Sum` 클래스를 구현할 수 있다.

```java
public class Monster implements Rotatable, Movable, Resizable {
    ...
}

public class Sun implements Movable, Resizable {
    ...
}
```

<br/>

> 참고 - 상속으로 코드 재사용 문제를 모두 해결할 수 있는것은 아니다. 예를 들어 한 개의 메서드를 재사용하려고 100 개의 
메서드가 정의되어 있는 클래스를 상속받는 것은 좋은 생각이 아니다. 이럴때는 **delegation**, 즉 멤버 변수를 이용해서 클래스에서 필요한 
메서드를 직접 호출하는 것이 좋다. 종종 `final` 로 선언된 클래스들이 이런 용도로 쓰이곤 한다. 다른 클래스가 상속받지 못하게 함으로써 
원래 동작을 유지하는것을 원하기 때문이다. (e.g `String`)


<br/>

## Resolution Rules

```java
public interface A {
    default String getName() {
        return "A";
    }
}

public interface B extends A { 
    default String getName() {
        return "B extends A";
    }
}

public class C implements B, A { 
    public static String getName() {
        return new C().getName(); 
    }
}
```
 
이 경우에는 어떤 `getName` 이 호출될까? 다음의 해석 규칙을 알고 있으면 쉽게 알 수 있다.

1. 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.  
2. 1번 규칙 이외의 상황에서는 서브인터페이스가 이긴다. 상속 관계를 갖는 인터페이스에서 같은 시그니처를 갖는 
메서드를 정의할 때는 서브 인터페이스가 이긴다.  
3. 여전히 디폴트 메서드의 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를 
오버라이드하고, 호출해야 한다.

위의 예제에서는 `B.getName` 이 호출된다.

<br/>

```java
public class C extends D implements B, A {
    public static String getName() {
        return new C().getName();
    }
}

interface A {
    default String getName() {
        return "A";
    }
}

interface B extends A {
    default String getName() {
        return "B extends A";
    }
}

class D implements A {}
```

이 경우에도 `B.getName` 이 호출된다. `D.getName` 은 `A.getName` 과 동일하다. 

<br/>

```java
public interface A {
    default String getName() {
        return "A';
    }
}

public interface B {
    default String getName() {
        return "B';
    }
}

public class C implements A, B {}
```

이 경우 `C.getName` 호출시 어떤 인터페이스의 디폴트 메서드를 사용할지 컴파일러가 결정할 수 없으므로 
`B.super.getName` 혹은 `A.super.getName` 을 직접 명시적으로 호출해야 한다.
