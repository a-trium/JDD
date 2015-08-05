# Rule 61 - 추상화 수준에 맞는 예외를 던져라
 
## 메서드가 하는일과 뚜렷한 관련성이 없는 예외가 메서드에서 발생하면 당혹스러울 수 있기 때문에, 상위 계층에서는 하위계층에서 발생하는 예외로 변경해서 던져야 한다.

```java
public E get(int index) {
    ListIterator<E> i = listIterator(index);
    
    try {
        return i.next();
    } catch(NoSuchElementException e) {
        throw new IndexOutOfBoundsException("index : " + index);
    }
}
```

<br/>

## 예외 연결

```java
try {
} catch(LowerLevelException cause) {
    throw new HigherLevelException(cause);
}

// client
Throwable cause = Throwable.getCause();
```

대부분의 표준예외들은 예외 연결을 위한 생성자를 가지고 있다.

```java
class HigherLevelException extends Exception {
    HigherLevelException(Throwable cause) {
        super(cause);
    }
}
```

그런 생성자가 없을 경우 `Throwable.initCause` 를 이용해 하위 예외를 연결하고, 스택트레이스에 추가할 수 있다.

> 아무 생각없이 하위 계층 예외를 밖으로 전달하는 것보다는 예외 변환이 낫지만, 그 보다 더 좋은 방법은 예외가 발생하지 않도록 
처리하는 것이다.



