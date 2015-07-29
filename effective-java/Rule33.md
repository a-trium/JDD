# ordinal을 배열 첨자로 사용하는 대신 EnumMap을 이용하라

`ordinal` 메서드를 배열의 인덱스로 이용하는 코드를 작성하면 안된다  
- 형 안전성을 보장하지 못함
- 출력 결과에 레이블을 수동으로 만들어 줘야함
- 정확한 `int`값이 사용되도록 해야함, 그렇지 않으면 `ArrayIndexOutOfBoundsException`예외를 냄

```java

// ordinal 값을 배열 인덱스로 사용 - 이러면 안됨
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        private static final Transitions[][] TRANSITIONS = {
            { null,     MELT,     SUBLIME },
            { FREEZE,   null,     BOIL },
            { DEPOSIT,  CONDENSE, null }
        };

        public static Transition from(Phase src, Phase dst){
            return TRANSITIONS[src.ordinal()][dst.ordinal()];
        }
    }
}

// EnumMap을 중첩해서 사용
public enum Phase{
    SOLID, LIQUID, GAS;

    public enum Transition{
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase src;
        private final Phase dst;

        Transition(Phase src, Phase dst){
            this.src = src;
            this.dst = dst;
        }

        private static final Map<Phase, Map<Phase, Transition>> m = new Map<Phase, Map<Phase, Transition>>(Phase.class);
        static {
            for (Phase p : Phase.values())
                m.put(p, new EnumMap<Phase, Transition>(Phase.class));
            for (Transition trans : Transition.values())
                m.get(trans.src).put(trans.dst, trans);
        }

        public static Transition from(Phase src, Phase dst){
            return m.get(src).get(dst);
        }
    }
}
```


> enum 상수를 어떤 값에 대응 시킬때는, EnumMap으로 만들자

- 내부적으로는 `ordinal`을 사용하므로 성능상 차이가 없음
- 소스코드가 더 짧고 안전함
