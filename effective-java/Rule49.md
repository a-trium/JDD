# 객체화된 기본 자료형 대신 기본 자료형을 이용하라

릴리즈 1.5 부터 **autoboxing** 과 **auto-unboxing** 이 언어의 일부가 되었다. 객체화된 기본 자료형과, 기본 자료형은 세 가지 차이점이 있다.
  
- 기본 자료형은 값만 가지지만, 객체화된 기본 자료형은 값 이외에도 **신원(identity)** 를 가진다. 따라서 값은 같더라도, 신원은 다를 수 있다.
- 기본 자료형에 저장되는 값은 기능적으로 모두 완전한 값이지만, 객체화된 기본 자료형에는 `null` 이 저장될 수 있다.
- 기본 자료형은 시간이나 공간 요구량 측면에서 객체 표현형보다 효율적이다.

```java
Long l1 = new Long(3L);
Long l2 = new Long(3L);

assertFalse(l1 == l2);

Long l3 = 3L;
Long l4 = 3L;

assertTrue(l3 == l4);
```

```java
Comparator<Integer> naturalOrdering = new Comparator<Integer>() {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o1 < o2 ? -1 : (o1 == o2 ? 0 : 1);
    }
};

assertEquals(-1, naturalOrdering.compare(new Integer(41), new Integer(42)));
assertNotSame(0, naturalOrdering.compare(new Integer(42), new Integer(42)));
```

부등식 비교시 객체화된 기본 자료형은, 기본 자료형으로 자동 변환된다. 그러나 `==` 비교시에는 값이 아니라 신원을 비교하기 때문에 문제가 발생할 수 있다. 따라서 
객체화된 기본 자료형에 `==` 연산자를 사용하는 것은 거의 항상 오류라고 봐야 한다.

```java
Comparator<Integer> naturalOrdering = new Comparator<Integer>() {
    @Override
    public int compare(Integer o1, Integer o2) {
        int x = o1;
        int y = o2;
        return x < y ? -1 : (x == y ? 0 : 1);
    }
};

assertEquals(-1, naturalOrdering.compare(new Integer(41), new Integer(42)));
assertEquals(0, naturalOrdering.compare(new Integer(42), new Integer(42)));
```

객체화된 기본 자료형의 초기값은 `null` 이므로 항상 주의해야한다. 만약 기본 자료형에 대입하려 한다면 `NPE` 를 발생시킬 것이다.

<br/>

객체화된 기본 자료형을 써야 할 경우는

- 컬렉션의 요소, 키, 값
- 형인자 자료형
- 리플렉션을 통해 메서드를 호출할 때
