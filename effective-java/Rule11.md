Rule11 clone을 재정의 할 때는 신중하라

* Cloneable : 어떤 객체가 복제를 허용한다는 사실을 알리는 데 쓰려고 고안된 믹스인 인터페이스
clone메서드를 구현하는 방법, clone메서드를 정의하면 좋은 경우를 알아보자.

문제점 :
- clone메서드가 없다.
- Object의 clone메서드가 protected로 구현되어있다.
- 리프렉션(reflection)을 사용하지 않고는 Cloneable을 구현한 객체라 해도 clone메서들르 호출할 방법이 없다.
- 이에 더해, 리플렉션 호출에서도 clone메서드가 구현되어있다는 보장이 없음

cloneable이 하는 일:
- protected로 선언된 Object의 clone메서드가 어떻게 동작할 지 정한다
- 어떤 클래스가 cloneable을 구현하면, Object의 clone메서드는 해당 객체를 필드 단위로 복사한 객체를 반환.
- 일반적으로 인터페이스를 구현하는 것으 클래스가 무슨일을 할 수 있는지, 클라이언트에게 알리는 것
- 그런데 cloneable의 경우, 상위 클래스의 protected멤버가 어떻게 동작할지 규정하는 용도로 사용

clone메서드의 규칙은 느슨하다:

x.clone() != x
-> 참이어야 함

x.clone().getClass() == x.getClass()
-> 반드시 참이어야하는 건 아님

x.clone().equals(x)
-> 반드시 참이어야하는 건 아님

객체를 복사하면 보통 같은 클래스의 새로운 객체가 만들어지는데, 내부 자료 구조까지 복사해야 될 수도 있음
clone이 만드는 복사본의 내부 객체는 생성자로 만들 수도 있음
클래스가 final로 선언되어 있다면, 생성자로 만든 객체를 반환하도록 clone을 구현할 수 도 있음

비-final클래스에 clone을 재정의할 때는 반드시 super.clone을 호출해 얻은 객체를 반환해야함
실질적으로 Cloneable인터페이스를 구현하는 클래스는 제대로 동작하는 Public clone메서드를 제공해야함

재정의 메서드(overriding method)의 반환값 자료형은 재정의 되는 메서드의 반환값 자료형의 하위 클래스가 될 수 있음
라이브러리가 할 수 있는 일은 클라이언트에게 미루지 말라는 것

clone메서드는 또 다른 형태의 생성자이다.
원래 객체를 손상시키는 일이 없도록 해야하고 복사본의 불변식(invariant)도 제대로 만족시켜야 한다.
stack의 clone메서드가 제대로 동작하도록 하려면 스택의 내부 구조도 복사해야 한다.
간단한 방법은 elements배열에도 clone을 재귀적으로 호출하는 것

@Override public Stack clone(){
	try {
		Stack result = (Stack) super.clone();
		result.elements = elements.clone();
		return result;
	} catch ( CloneNotSupportedException e ){
		throw new AssertionError();
	}
}

clone의 아키텍처는 변경 가능한 객체를 참조하는 final필드의 일반적 용법과 호환되지 않는다.

각 버킷을 구성하는 연결 리스트까지도 복사해야 한다.

적절한 크기의 새로운 buckets배열을 할당하고 원래 buckets배열을 돌면서 비어있지 않으 ㄴ모든 버킷에 깊은 복사를 실행
단, 리스트가 길면 쉽게 스택오버플로(stack overflow)가 발생

마지막 방법 : super.clone호출 결과를 반환된 객체의 모든 필드를 초기 상태로 되돌려 놓은 다음에, 상위레벨(higher-level)메서드를 호출해서 객체 상태를 다시 만드는 것
생성자와 마찬가지로, clone메서드는 복사본의 비-final메서드, 즉 재정의 가능 메서드를 복사 도중에 호출해서는 안된다

+ 하나 더! : 다중 스레드에 안전해야 하는(thread-safe)클래스를 Cloneable로 만들려면, clone메서드에도 동기화(synchronization)메커니즘을 적용해야 한다.
+ 객체를 복사할 대안을 제공하거나, 아예 복제 기능을 제공하지 않는 것이 낫다.
+ 객체 복제를 지원하는 좋으 ㄴ방법은, 복사생성자(copy constructor)나 복사 팩터리(copy factory)를 제공하는 것

복사생성자와 복사 팩터리 메서드 접근법은 Cloneable/clone보다 좋은 점이 많다.
- 위험해 보이는 언어외적(extralinguistic)객체 생성 수단에 의존하지 않음
- 제대로 문서화되어 있지도 않고 강제하기도 어려운 규약에 충실할 것을 요구하지 않음
- final필드 용법과 충돌하지 않음
- 불필요한 예외검사요구하지 않음
- 형변환 필요 없음

Cloneable을 계승하는 인터페이스는 만들지 말아야 하며, 계승목적으로 설계하는 클래스는 Cloneable을 구현하지 말아야 함

