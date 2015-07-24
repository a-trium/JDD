# int 상수 대신 enum을 사용하라

## int 상수의 문제점
1. 오렌지를 기대하는 메서드에 사과를 넘겨도 컴파일러가 에러를 내지 않는다
2. int enum 그룹별로 `namespace`를 제공하지 않기 때문에, 이름이 충돌할 수 있다
3. 컴파일 시점 상수이기 때문에, 값이 변경되면 클라이언트도 다시 컴파일 해야 한다
4. int enum상수를 문자열로 변환하기 쉽지 않아, 디버거로 확인해도 숫자만 보인다


## String 상수의 문제점
1. 상수 이름을 화면에 출력할 수 있지만, 상수 비교시 문자열 비교해야 하므로 성능이슈가 생긴다
2. 프로그래머가 필드 이름 대신 하드코딩된 문자열 상수를 클라이언트 코드에 박아둘 수 있다
3. int상수보다 더 나쁜 패턴이다


## java enum의 장점
1. 열거상수 별로 `public static final`필드 형태이다
2. 생성자가 존재하지 않아, 상속을 통한 확장이 막혀있다
3. 이미 선언된 enum 상수 이외의 객체를 사용할 수 없으므로 통제가 가능하다
4. 컴파일 시점 형 안전성을 제공한다
5. `namespace`가 분리되어 있기때문에, 이름 충돌이 일어나지 않는다
6. `toString`을 통해 쉽게 문자열로 변경가능하다
7. 메서드나 필드를 추가하여 임의의 인터페이스 구현이 가능하다
8. `Obejct 메서드`가 포함되어 있다
9. `Comparable` `Serializable` 인터페이스가 구현되어 있다

`예제`

```java
public enum Planet {
    MERCURY (3.302e+23, 2.439e6),
    VENUS   (4.869e+24, 6.052e6),
    EARTH   (5.975e+24, 6.378e6),
    MARS    (6.419e+23, 3.393e6),
    JUPITER (1.899e+27, 7.149e7),
    SATURE  (5.683e+25, 6.027e7),
    URANUS  (8.683e+25, 2.556e7),
    NEPTUNE (1.024e+26, 2.477e7);

    private static final double G = 6.67300E-11;

    private final double mass;
    private final double radius;
    private final double surfaceGravity;

    Planet(double mass, double radius){
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() { return mass; }
    public double radius() { return radius; }
    public double surcfaceWight(double mass){ return mass * surfaceGravity; }
}
```

- enum에 상수 데이터를 넣으려면 `instance field`를 선언하고 생성자를 통해 필드를 초기화 한다.
- 클라이언트에게 공유할 필요가 없다면 `private`, `package-private`으로 선언
- switch문을 사용하기 보다는, `absctract 메서드`를 선언하고, ***각 상수별 클래스 몸체*** 안에서 실제 메서드로 재정의해라 (아래 예제 참고)
`예제`
```java
// 방법 1
public enum Operation{
    PLUS    { double apply(double x, double y){return x+y;} },
    MINUS   { double apply(double x, double y){return x-y;} },
    TIMES   { double apply(double x, double y){return x*y;} },
    DIVIDE  { double apply(double x, double y){return x/y;} };

    abstract double apply(double x, double y);
}

// 방법 2
// toString 메서드 작동시 괄호 안에 적은 기호가 나온다
public enum Operation{
    PLUS("+")    { double apply(double x, double y){return x+y;} },
    MINUS("-")   { double apply(double x, double y){return x-y;} },
    TIMES("*")   { double apply(double x, double y){return x*y;} },
    DIVIDE("/")  { double apply(double x, double y){return x/y;} };

    abstract double apply(double x, double y);
}
```

여러 enum 상수끼리 공유하는 코드를 만들때 위 방법으로 해결하기 어렵다. 이럴때는 다음과 같이 private으로 선언된 중첩 enum을 만든다.
```java
public enum PayrollDay{
    MONDAY(PayType.WEEKDAY), TUESDAY(PayType.WEEKDAY), WEDNESDAY(PayType.WEEKDAY), THURSDAY(PayType.WEEKDAY), FRIDAY(PayType.WEEKDAY), SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);

    private final PayType payType;
    PayrollDay(payType payType){ this.payType = payType; }

    double pay(double housWorked, double payRate){ return payType.pay(housWorked, payRate); }

    private enum PayType{
        WEEKDAY {double overtimePay(double hours, double payRate) {return hours <= HOURS_PER_SHIFT ? 0 : (hours - HOURS_PER_SHIFT)  * payRate / 2; }}
        , WEEKEND {double overtimePay(double hours, double payRate) { return hours * payRate / 2; }
        };
        private static final int HOURS_PER_SHIFT = 8;

        abstract double overtimePay(double hrs, double payRate);

        double pay(double housWorked, double payRate){
            double basePay = hoursWorked * payRate;
            return basePay + overtimePay(hoursWorked, payRate);
        }
    }
}
```
