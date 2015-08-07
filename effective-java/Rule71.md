# 규칙 71. 초기화 지연은 신중하게 하라

초기화 지연(lazy initalization)이란 : 필드 초기화를 실제로 그 값이 쓰일 때 까지 미루는 것
- 값을 사용하는 곳이 없다면 초기화되지 않음
- static필드와 객체 필드 모두 적용 가능
- 초기화 과정에서 발생하는 순환성(circularity)을 해소하기 위해 사용
- 객체 생성 비용은 줄이지만, 사용 비용이 증가

초기화 순환성(initialization circularity) 문제를 해소하기 위해 초기화를 지연시키는 경우 동기화된 접근자(synchronized accessor)를 사용하라.

```java

private FieldType field;

synchronized FieldType getField(){
    if(field == null)
        field = computeFieldValue();
    return field;
}
```

성능 문제 때문에 정적 필드 초기화를 지연시키고 싶을 때는 초기화 지연 담당 클래스(lazy initialization holder class) 숙어를 적용하라

```java
private static class FieldHolder{
    static final FieldType field = computeFieldValue();
}
static FieldType getField() { return FieldHolder.field; }
```

`FieldHolder`클래스는 `FieldHolder.field`가 처음으로 호출되는 순간 초기화 된다. 이방식이 좋은점은 `getField`를 동기화 메서드로 선언하지 않아도 된다는 것이다. 따라서 초기화를 지연시켜도 메서드이용 비용이 증가하지 않는다는 점이다. 만약 성능 문제 때문에 객체 필드 초기화를 지연시키고 싶다면 `이중 검사(double-check)`를 사용하라.

```java
FieldType volatile FieldType field;

FieldType getField(){
    FieldType result = field;
    if(result == null) {        // 첫번째 검사(락 없음)
        synchronized(this){
            result = field;
            if(result == null)  // 두번째 검사(락)
                field = result = computeFieldValue();
        }
    }
}
```

1.5이전에는 `volatile`이 가진 의미가 이중검사 패턴을 지원할 만큼 강력하지 않았지만, 1.5 이후 버전부터 도입된 메모리 모델 덕문에 문제가 해결되었다. // 무슨문제?

이중 검사 숙어의 주의점
- 여러번 초기화되어도 상관없는 객체 필드를 초기화를 지연시키고 싶을때 -> 두번째 검사를 없애도 된다.(단일검사(single-check))
