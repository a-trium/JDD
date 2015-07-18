# 규칙22. 멤버 클래스는 가능하면 static으로 선언하라

`중첩 클래스(nested class)` : 다른 클래스 안에 정의된 클래스로, 해당 클래스가 속한 클래스 안에서만 사용된다. **외부에서 사용된다면 nested class를 만들면 안된다.**  
종류  
- 정적 멤버 클래스(static member class)
- 비-정적 멤버 클래스(nonstatic member class)
- 익명 클래스(anonymous class)
- 지역 클래스(local class)

#### 정적 멤버 클래스(static member class)
가장 간단한 중첩 클래스, 바깥 클래스의 `private`포함 모든 멤버에 접근할 수 있다.  
바깥 클래스의 정적 멤버이며, 다른 정적 필드와 동일한 접근권한 규칙을 따른다.  
`예` : public 도움 클래스(helper class)

</br>
#### 비 정적 멤버 클래스(nonstatic member class)
`어댑터(adapter)`를 정의할때 많이 사용되며, 바깥 클래스 객체를 다른 클래스 객체인것처럼 보이게 하는 용도로 쓰인다.
```java
public class MySet<E> extends AbstractSet<E>{
    ... // 생략
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<E> {
        ... // 생략
    }
}
```
**바깥 클래스 객체에 접근할 필요가 없는 멤버 클래스를 정의할 때는** `항상` **선언문 앞에 static을 붙여서 비-정적 멤버 클래스 대신 정적 멤버 클래스로 만들자**
static을 생략하면 모든 객체는 내부적으로 바깥 객체에 대한 참조를 유지하게되므로, `garbage collection`이 힘들어진다.

</br>
#### 익명 클래스(anonymous class)
이름이 없는 클래스로, 멤버로 선언하지 않으며, 사용하는 순간에 선언하고 객체를 만든다. `비-정적 문맥(nonstatic context)`안에서 사용될 때만 바깥 객체를 갖게되지만, static 멤버를 가질 수는 없다.  
Rule21에 `전략패턴(stragety Pattern)` 또는 `Runnable`, `Thread`, `TimerTask`와 같은 `프로세스 객체`를 만들때 주로 사용되며, 정적 팩토리 메서드 안에서도 많이 쓰인다(Rule 18에 `intArrayAsList`메서드 참고)

</br>
#### 지역 클래스(local class)
가장 사용빈도가 낮다. 지역변수와 동일한 `유효범위 규칙(scoping rule)`을 따른다.
