# 규칙20. 태그 달린 클래스 대신 클래스 계층을 활용하라

**예제**
```java
// 태그 달린 클래스 - 클래스 계층을 만드는 쪽이 더 낫다!!!
class Figure {
    private enum Shape { RECTANGLE, CIRCLE };

    // 어떤 모양인지 나타내는 태그 필드
    private final Shape shape;

    // 태그가 RECTANGLE일때만 사용되는 필드들
    private double length, width;

    // 태그가 CIRCLE일때만 사용되는 필드들
    private double radius;

    // 원을 만드는 생성자
    public Figure(double radius) {
        this.radius = radius;
    }

    // 사각형을 만드는 생성자
    public Figure(double length, double width) {
        this.length = length;
        this.width = width;
    }

    public double area() {
        switch(shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError();
        }
    }
}
```

위 클래스의 문제점
1. enum 선언, 태그 필드, switch문 등 상투적 코드 반복되며
2. 서로 다른 기능이 한 클래스에 모여있어서 가독성이 떨어짐
3. 객체를 만들때마다 필요없는 필드가 함께 생성 -> 메모리 낭비
4. 생성자에서 관련없는 필드를 초기화하지 않는 한, final필드 할당 불가능
5. 태그 기반 클래스에 새로운 기능 추가시, 소스파일 반드시 수정해야함 (switch문 추가)
6. 자료형만 봐서는 해당 객체가 무슨 기능을 제공하는지 알 수 없다

간단히 말해서, **태그 기반 클래스(tagged class)는 너저분한데다 오류 발생 가능성이 높고, 효율적이지도 못하다.**

</br>
태그 기반 클래스를 클래스 계층으로 변환한 예제
```java
abstract class Figure {
  abstract double area();
}

class Circle extends Figure {
    final double radius;

    Circle(double radius) { this. radius = radius; }

    public double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this. length = length;
        this. width = width;
    }

    public double area() { return length * width; }
}
```
태그기반 클래스에 모든 단점이 사라졌으며, 프로그래머들이 이 클래스의 계층을 확장할 때 최상위 클래스 `Figure`의 소스코드를 안보고도 독립적으로 일하면서 협력할 수 있다. 또한 하위 클래스마다 별도의 자료형이 있으므로 변수가 가진 기능이 무엇인지 명시적으로 표현할 수 있다.
