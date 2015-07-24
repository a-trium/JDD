# Rule 24 - Unchecked Warning 을 제거하라

다양한 무점검 경고가 있다.

- unchecked cast warning
- unchecked method invocation warning
- unchecked generic array creation warning
- unchecked conversion warning

이런 무점검 경고는 **가능하다면 모두 제거해야 한다.** 그러면, 코드의 형 안전성(**type safety**) 가 보장되는 것이므로 좋아지고, 
런타임에 `ClassCastException` 이 발생하지 않을 것이다.

## 제거할 수 없는 경고는 형 안전성이 확실할 때만 @SupressWarnings("unchecked") 으로 제거하라.

하지만 `SupressWarnings` 어노테이션은 가능한 작은 범위에 적용해야한다. 절대로 클래스 전체에 적용하면 중요한 경고 메세지를 놓치게 될 것이다.

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size)
        return (T[]) Arrays.copyOf(elements, size, a.getClass())
        
    System.arraycopy(elements, 0, a, 0, size);
    
    if (a.length > size)
        a[size] = null;
        
    return a;
}

// after
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
      @SupressWarnings("unchecked") T[] result = 
        (T[]) Arrays.copyOf(elements, size, a.getClass());
        
      return result;
    }
        
    System.arraycopy(elements, 0, a, 0, size);
    
    if (a.length > size)
      a[size] = null;
        
    return a;
}
```

그리고 `@SupressWarnings` 어노테이션을 사용할 때마다 왜 형안전성을 위반하지 않는지 밝히는 주석을 반드시 붙여라.