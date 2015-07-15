Rule 6 유효기간이 지난 객체 참조는 폐기하라

- 예 : 스택(stack)

-> 문제 : 메모리 누수(memory leak)
-> 현상 : 성능저하, 메모리 요구량 증가, 디스크페이징(disk paging), OutOfMemoryError
-> 원인 : 스택이 만기참조(obsolete reference)를 제거하지 않게 때문( : 즉, 스택이 커졌다가 줄어들며 제거한 객체들, 다시 이용되지 않을 참조를 쓰레기 수집기가 처리 하지 못함 )
-> 해결방법 : 쓸 일 없는 객체는 무조건 Null로 만들기

public Object pop(){
	if ( size == 0 )
		throw new EmptyStackException();
	Object result = elements[--size];
	elements[size] = null; // 만기 참조 제거
	return results;
}

-> 더 좋은 해결방법 : 만기참조를 제거 하는 가장 좋은 방법은 해당 참조가 보관된 변수가 유효범위(scope)를 벗어나게 두는 것(변수 정의 시 유효범위 최대한 좁게 정의)

- 예 : 캐시(cashe)

-> 해결방법1 : WeakHashmap을 가지고 캐시를 구현하는 것. 단, 캐시 바깥에서 키를 참조하고 있을 때만 값(value)를 보관하면 될 때 쓰는 전략
-> 하지만, 일반적으로 캐시에 보관되는 항목의 수명은 캐시에 보관된 기간에 따라 결정됨
-> 그래서 해결방법2 : 후면스레드로 처리, 캐시에 새로운 항목을 처가할 때 처리(Linkedhashmap클래스의 removeEldestEntry메서드 사용하면 좋다.)

- 예 : 리스너(listener)등의 역호출자

-> 해결방법 : 역호출자에 대한 약한참조(weak reference)만 저장하는 것(WeakHashMap의 키로 저장하는 것이 그 예)


