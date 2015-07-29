# 비트 필드(bit field)대신 EnumSet을 사용하라

전통적으로 `int enum`방식 : 각 상수에 2의 거듭제곱 값을 대입하는 것
```java
public class Text{
    public static final int STYLE_BOLD            = 1 << 0;       // 1
    public static final int STYLE_ITALIC          = 1 << 1;       // 2
    public static final int STYLE_UNDERLINE       = 1 << 2;       // 4
    public static final int STYLE_STRIKETHROUGH   = 1 << 3;       // 8

    // 인자로 STYLE_상수를 비트별로 OR한 값이거나 0
    public void applyStyles(int styles){ ... }
}
```

비트 단위 산술 연산을 통해 `합집합`이나 `교집합` 등의 `집한연산`도 효율적으로 실행할 수 있다.

```java
    text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

`비트 필드`는 `int enum`패턴과 똑같은 단점을 갖고 있다.
- 출력한 결과를 보기 어렵다 (숫자로 출력됨)
- 필드에 포함된 모든 요소를 순차적으로 보기 어렵다 (모두 출력해야함)

### EnumSet
- Set 인터페이스를 구현하여, `Set`이 제공하는 기능을 그대로 사용
- 형 안전성성, 상호 운용성(interoperability) 제공
- 내부적으로 `비트 벡터(vit vector)` 사용 (enum값이 64개 이하인 경우 `long`하나만 사용함)

```java
// EnumSet 적용
public class Text{
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    public void applyStyles(Set<Style> styles){ ... }
}
```

`applyStyles`메서드가 `EnumSet<Style>`이 아니라 `Set<Style>`것에 유의하자.
`EnumSet`에는 ***정적 팩터리 메서드*** 가 존재하므로, 편하게 객체를 만들 수 있다.

```java
  text.applyStyles(EnumSet.of(STYLE_BOLD, STYLE_ITALIC));
```

요약
>열거 자료형을 집합에 사용해야 한다고 해서 비트 필드로 표현하면 곤란하다
