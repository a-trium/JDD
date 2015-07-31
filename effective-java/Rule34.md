# Rule 34 - 확장 가능한 enum 을 만들어야 한다면 인터페이스를 이용하라

일반적으로 `enum` 을 확장하는것은 좋은 생각이 아니다.

- 확장된 자료형의 상수들이 기본 자료형의 상수가 될 수 있다는 점. 그러나 그 반대는 불가능하다는점에서 혼란스러움
- 자료형과 확장된 자료형의 enum 상수를 순차적으로 살펴볼 방법도 없다
- enum 의 상속을 허용하게 되면 설계와 구현에 관계된 많은 부분들이 까다로워짐

확장되면 좋은 경우가 하나 있는데, **연산 코드** 를 구현하는 경우다. 이 경우 인터페이스를 이용할 수 있다.

```java
public interface Operation {
    double apply(double x, double y);
}

public enum BasicOperation implements Operation{
    PLUS("+") { @Override public double apply(double x, double y) { return x + y; } },
    MINUS("-") { @Override public double apply(double x, double y) { return x - y; } },
    TIMES("*") { @Override public double apply(double x, double y) { return x * y; } },
    DIVIDE("/") { @Override public double apply(double x, double y) { return x / y; } };

    private String symbol;
    BasicOperation(String symbol) { this.symbol = symbol; }
    @Override public String toString() { return this.symbol; }
}

public enum ExtendedOperation implements Operation{
    EXP("^") { @Override public double apply(double x, double y) { return Math.pow(x, y); } },
    REMINDER("%") { @Override public double apply(double x, double y) { return x % y; } };


    private String symbol;
    ExtendedOperation(String symbol) { this.symbol = symbol; }
    @Override public String toString() { return this.symbol; }
}
```

```java
// client code

test(ExtendedOperation.class, 3, 5)

pubilc <T extends Enum<T> & Operation> test(Class<T> opSet, double x, double y) {
    ...
    ...
}
```

