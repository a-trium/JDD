# Rule 18 - 추상 클래스 대신 인터페이스를 사용하자

## 1. 인터페이스는 **mixin** 을 정의하는데 이상적이다. 

```scala
// composition via inheritance: bakery depends on crop and livestock modules
trait BakeryModule extends CropModule with LivestockModule {
     lazy val bakery = new Bakery(mill, dairyFarm)
}   
 
// abstract member: we need a bakery
trait CafeModule {
     lazy val espressoMachine = new EspressoMachine()
     lazy val cafe = new Cafe(bakery, espressoMachine)
 
     def bakery: Bakery
}
 
// the abstract bakery member is implemented in another module
object CafeApp extends CafeModule with BakeryModule {
     cafe.orderCoffeeAndCroissant()
}
```

## 2. 인터페이스는 비계층적인 자료형 프레임워크를 만들 수 있도록 한다.

```java
public interface Singer {}
public interface SongWriter {}

public interfaceSingerSongwriter extends Singer, SongWriter {}
```

## 3. 인터페이스 내부에 메서드 구현을 두려면, 추상 골격 클래스(Abstract Skeletal Implementation) 을 이용하자 

**참고: Java8 에서는 인터페이스에 메서드 구현을 할 수 있다** [Java 8 Default Method](http://zeroturnaround.com/rebellabs/java-8-explained-default-methods/)

추상 골격클래스의 예제는 다음과 같다.

- `AbstractCollection`, `AbstractSet`, `AbstractList`, ...

```java
public abstract class AbstractMepEntry<K, V> implements Map.Entry<K, V> {
     ...
     ...
}
```

요약하자면, 인터페이스는 다양한 구현이 가능한 자료형을 정의하는 일반적으로 가장 좋은 방법이다. 유연하고 강력한 API 를 만드는 것 보다 개선이 쉬운 API 를 만드는 것이 
중요한 경우는 예외다. 그런 경우에는 추상 클래스를 사용해야 하는데, 그 단점을 잘 이해하고 사용할 수 있는 경우만 한정해서 사용해야 한다. 

**중요한 인터페이스를 API 에 포함시키는 경우에는 골격 구현 클래스를 함께 제공하면 어떨지 심각하게 고려** 해야한다. 또한 `public` 인터페이스는 
극도로 주의해서 설계해야 하며, 실제로 여러 구현을 만들어 보며 광범위하게 테스트 해야 한다.

Q. 골격 클래스가 인터페이스보다 더 나은점?


