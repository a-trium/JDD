# Rule 14 - 퍼블릭 클래스 안에는 public 필드를 두지 말고 접근자 메소드를 사용하라

```java
// 하지 말 것

class Point {
    public double x;
    public double y;
}
```

- 클라이언트가 사용하는 API 를 변경하지 않고서는 내부 표현을 변경할 수 없고
- 불변식(invariant) 도 강제 불가능하고
- 필드를 사용하는 순간에 어떤 동작이 실행되도록 하는것도 불가능

`java.awt` 패키지에 포함된 `Point` 와 `Dimension` 클래스가 내부 필드를 공개하는 대표적인 예.

**선언된 패키지 밖에서도 사용 가능한 클래스에는 Setter, Getter 를 제공하자.** 하지만 **package-private** 이나 **nested private class** 는 
데이터 필드를 공개하지 않아도 잘못이라 말할 수 없다.







