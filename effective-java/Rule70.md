# 규칙 70. 스레드 안전성에 대해 문서로 남겨라

클래스와 사용자 사이의 규약 가운데 중요한것 중 하나는, 클래스의 객체나 정적 메서드가 병렬적으로 이용되었을 때 어떻게 동작하느냐 하는것이다.

- `synchronized`키워드는 메서드의 구현 상세(implementation detail)에 해당하는 정보이며, 공개 API의 일부가 아니다. -> 다중스레드에 안전하다는 뜻이 아니다
- 병렬적으로 사용해도 안전한 클래스가 되려면, 어떤 수준의 스레드 안전성을 제공하는 클래스인지 문서로 남겨야 한다


스레드 안성정 수준별 요약
- 변경 불가능(immutable) : 이 클래스로 만든 객체들은 상수이므로 동기화 메커니즘 없이 병렬적 이용이 가능 (`ex` String, Long, BigInteger ...)
- 무조건적 스레드 안전성(unconditionally thread-sfae) : 이 클래스의 객체들은 변경가능하지만 적절한 내부 동기화 메커니즘을 갖고있어서 병렬적 사용 가능
- 조건부 스레드 안전성(conditionally thread-sfae) : 무조건적 스레드 안전성과 거의 같은 수준이나 몇몇 스레드는 외부에서 동기화 처리해야 함(`ex` Collections.synchronized계열 메서드 -> `iterator`)
- 스레드 안전성 없음 : 변경가능한 객체이므로, 병렬적으로 사용하기 위해서는 클라이언트가 외부적 동기화 수단으로 감싸야함 (`ex` ArrayList, HashMap)
- 다중 스레드에 적대적(thread-hostile) : 동기화 없이 정적 데이터를 변경하기 떄문에 외부적 동기화 수단으로 감싸더라도 안전하지 않음. (`ex` System.runFinalizersOnExit 지금은 폐기(deprecated)됨)

조건부 스레드 안전성 클래스에 대한 문서를 만들때는 신중히 작성해야함
- 호출시 외부 어떠한 동기화 메커니즘을 동원해야 하는지 명시
- 순서대로 실행하려면 어떤 락을 사용해야 하는지 명시

`추가내용`
외부로 공개한 락을 통해 동기화하도록 하는 클래스의 경우, 유연성이 있긴하지만 대가도 따른다.
- `ConcurrentHashMap`, `ConcurrentLinkedQueue` 같은 병행 컬렉션에 사용하는 내부적인 고속 병행성 제어 메커니즘과 어울리지 않음
- 클라이언트가 락을 오랫동안 들고 있으면 `Dos공격(denial-of-service attack)`도 가능

> 이러한 공격을 막기위해 동기화 메서드 대신 `private 락 객체(private lock object)을 이용해야함`

```java
private final Ojbect lock = new Ojbect();

public void foo(){
    synchronized(lock){
        ...
    }
}
```

>> 상속을 염두해두고 클래스를 설계해야 한다
