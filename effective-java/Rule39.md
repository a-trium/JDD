# Rule 39 - 필요하다면 방어적 복사본을 만들어야 한다

> 여러분이 만드는 클래스의 클라이언트가 **불변식(invariant)** 를 망가뜨리기 위해 최선을 다할 것이라는 가정하에 
방어적으로 프로그래밍해야한다.

## 공격 가능한 예제

```java
public Period(Date start, Date end) {
    if (start.comparedTo(end) > 0) {
        throw new IllegalArgumentException();
    }
    
    this.start = start;
    this.end = end;
}

// attack
Date start = new Date();
Date end = new Date();

Period p = new Period(start, end);
end.setYear(78); // p 의 내부를 변경
```

## 생성자 인자를 사용한 공격을 방어하는 예제

따라서 `Period` 객체의 내부를 보호하려면, 생성자로 전달되는 변경 가능 객체를 반드시 방어적으로 복사해야 한다.

```java
public Period(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
    
    if (this.start.compareTo(this.end) > 0) 
        throw new IllegalArgumentException();
}
```

**Date 클래스는 final 클래스가 아니므로 clone 메소드를 이용하면 안된다.** `java.util.Date` 가 아니라 공격을 위해 설계된 하위 클래스 일수도 있다. 따라서 
**인자로 전달된 객체의 자료형이 제 3자가 계승할 수 있는 자료형인 경우, 방어적 복사본을 만들 때 clone 을 이용하지 않도록 해야한다.**

## 접근자를 이용한 공격

```java
public Date start() {
    return this.start;
}

public Date end() {
    return this.end;
}

// attack
Date start = new Date();
Date end = new Date();

Period p = new Period(start, end);
p.end().setYear(78) // p 의 내부를 변경
```

따라서 접근자에서도 방어적 복사본을 만들면 된다.

```java
public Date start() {
    return new Date(this.start.getTime());
}

public Date end() {
    return new Date(this.end.getTime());
}
```

이 경우 `clone` 을 이용해도 된다. `Period` 내부는 `Date` 객체임이 보장되기 때문이다. 그러나 **Rule 11** 에서 설명했듯이 정적 팩토리 메소드를 이용하는 편이 더 낫다.

<br/>

## Summary

클라이언트로부터 구했거나, 클라이언트에게 반환되는 변경 가능 컴포넌트가 있는 경우, 해당 클래스는 그 컴포넌트를 반드시 방어적으로 복사해야 한다. 
복사 오버헤드가 너무 크고, 클래스 사용자가 그 내부 컴포넌트를 부적절하게 변경하지 않는다는 보장이 있을 때는, 방어적 복사를 하는 대신 클라이언트 측에서 해당 
컴포넌트를 변경해서는 안 된다느 사실을 클래스 문서에 명시하고 넘어갈 수도 있다.


