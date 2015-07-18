# 규칙21. 전략을 표현하고 싶을 때는 함수 객체를 사용하라

프로그램 언어 가운데는 `함수 포인터(funtion pointer)`, `대리자(delegate)`, `람다 표현식(lambda expression)` 처럼, 특정 함수를 호출/저장하고 전달할 수 있도록 하는 것들이 있다.  
**함수 객체의 주된 용도** 는 `전략 패턴(stragety Pattern)`을 구현하는 것이다.

```java
class StringLengthComparator {
    public int compare(String s1, String s2) {
        return s1.length() - s2.length();
    }
}
```
위와같이 필드가 없는 `무상태(stateless)`클래스를 이용하여 실행 가능 전략(concrete stragety)을 사용할 수 있다.
위의 클래스를 `전략패턴(stragety Pattern)`으로 사용하려면, **전략 인터페이스(stragety interface)** 를 정의할 필요가 있다.

```java
public interface Comparator<T> {
    public int compare(T t1, T t2);
}
```
이 인터페이스는 `java.util`패키지에 포함된 것과 일치하는 `제너릭(generic)` 인터페이스 이다. 이를 사용하기 위해 `StringLengthComparator`클래스의 선언부를 조금 고치면 다음과 같다
```java
class StringLengthComparator implements Comparator<String>{
    ... // 아래는 위와 동일
}
```
이와 같이 사용하면 전략패턴을 사용할 수 있게 된다. 이와 같은 `실행 가능 전략` 클래스는 `익명 클래스(anonymous class)`로 정의하는 경우가 많다. 아래 예제를 보자.
```java
Arrays.sort(stringArray, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return s1.length() - s2.length();
    }
});
```
**tip**  
익명클래스를 사용하면 sort를 호출할 때 마다 새로운 객체가 만들어 지므로, 여러번 수행된다면 함수 객체를 `private static final 필드`로 선언하고 재사용 하는것을 고려해보자.  

`전략 인터페이스`는 `실행 가능 전략` 객체들의 자료형 구실을 한다. 따라서 굳이 public으로 만들 필요가 없다.

```java
class Host {
    private static class StrLenCmp implements Comparator<String>, Serializable {
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
    // 직렬화 기능 추가된 function
    public static final Comparator<String> STRING_LENGTH_COMPARATOR = new StrLenCmp();
}
```
