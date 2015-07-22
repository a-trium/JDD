# Rule 19 - 인터페이스는 자료형을 정의할 때만 사용해라

## 1. 잘못된 예

```java
public interface PhysicalConstants {
    static final double AVOGADROS_NUMBER = 6.02214199e23;
}
```

**이러지 말자!** 자바 플랫폼 라이브러리에도 상수 인터페이스가 몇개 있는데, `java.io.Object.StreamConstants` 가 그 예다. 실수니까 따라하지 말자.

```java
public class PhysicalConstants {
    private PhysicalConstants() {} 
    
    static final double AVOGADROS_NUMBER = 6.02214199e23;
}
```

**static import** 를 사용하면, 더 편하게 쓸 수 있다.

## 2. 요약

인터페이스는 자료형을 정의할 때만 사용해야 한다. 특정 상수를 API 의 일부로 공개할 목적으로는 적절치 않다.