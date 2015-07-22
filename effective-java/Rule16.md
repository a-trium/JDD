# Rule 16 - 계승하는 대신 구성하라

## 1. 메소드 호출과 달리, 계승은 캡슐화 원칙을 위반한다

상위 클래스의 구현은 시간이 지남에 따라 바뀔 수 있는데, 하위 클래스 코드는 수정된 적 없어도 망가질 수 있다. 따라서 
상위 클래스 작성자가 계승을 고려해 클래스를 설계하고 문서까지 만들어 놓지 않았다면 하위 클래스는 계속 변경되야 한다.

구체적인 사례로, `HashSet.addAll` 은 `HashSet.add` 를 통해 구현되어 있지만, `HashSet` 문서에는 그런 사실이 나와있지 않다.

또한 상위 클래스에 새로운 메소드가 추가되면 보안이 꺠질 수 있다. 예를 들어 하위 클래스에서, 원소를 추가하는 메소드에서
불변식을 검사한다고 할 때, 상위 클래스에 메소드가 추가되면 불변식을 우회하여 원소를 추가할 수 있다.

## 2. 따라서 구성(Composition) 을 이용하자

새로운 클래스에 기존 클래스를 참조하는 `private` 필드를 두어 사용하면 된다. 

```java
public class InstrumentSet<E> extends ForwardingSet<E> {
     private int addCount = 0;
     public InstrumentSet (Set<E> s){
          super(s);
     }
     @Override public boolean add(E e){
          addCount++;
          return super.add(e);
     }
     ..
}

public class ForwardingSet<E> implements Set<e>
{
     private final Set<E> s; // 중요!! - 구성을 사용
     public ForwardingSet(Set<E> s){ this.s = s;}

     public void clear(){ s.clear();}
     public void add(E e){ return s.add(e); }
     ...
}
```

`InstrumentedSet` 과 같은 클래스를 포장 클래스라고 부른다. (**decorator** 패턴). 포장 클래스에는 단점이 별로 없으나, **callback framework** 와 함께 
사용하기에는 적합하지 않다. 역호출 프레임워크에서 객체는 자기 자신에 대한 참조를 다른 객체에 넘겨 나중에 필요할 때 역호출 하도록 요청하는데, 
**wrapped object** 는 **wrapper* 에 대해서는 모르기 때문에, 전달 시 *wrapper object** 를 제외하고 전달한다. (*SELF* 문제라고 알려져 있다고 함.)

## 3. 상속은 **IS-A** 관계에서만 사용하자

자바에서는 이를 위반하는 사례들이 있다. 예를 들어 `Stack` 은 `Vector` 가 아닌데, 자바 플랫폼에서는 상속 관계로 구현되어 있다. 또한 `Properties` 는 해시테이블이 아니므로 
`HashTable` 을 상속하면 안된다. 설계자의 의도는 `String` 을 키와 값으로 사용하는 것이었지만, `HashTable` 에 접근할 수 있으므로 이 불변식은 깨질 수 있다.

이런 문제를 피하려면 *Composition*, *Forwarding* 을 이용하는 것이 좋다. 포장 클래스 구현에 적당한 인터페이스가 있다면 더욱 그렇다. 포장 클래스는 하위 클래스보다 
견고할 뿐 아니라, 더 강력하다