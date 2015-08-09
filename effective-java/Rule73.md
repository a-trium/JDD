# 규칙 73. 스레드 그룹은 피하라

스레드 시스템이 제공하는 기본적인 추상화 단위
- 스레드(thread)
- 락(lock)
- 모니터(monitor)
- 스레드 그룹(thread group)

**스레드 그룹(thread group)**
`애플릿(appley)`을 `격리시켜(isolating)` 보안문제를 피하고자 고안된 것

> 현재는 폐기된 추상화 단위이다.


역설적이게도 ***ThreadGroup API의 스레드 안전성은 취약하다***
스레드 그룹에 속한 `활성(active) 스레드`목록을 얻으려면 `enumerate 메서드`를 호출해야하는데, 호출된 순간 그 값이 정확한지 보장할 수 없다.
1.5 이전에는 `ThreadGroup.uncaughException 메서드`를 통해 어떤 스레드가 무점검 예외(uncaught exception)을 던졌을때 제어권을 가져오는 유일한 수단이였지만 1.5부터 Thread에 `setUncaughtExceptionHandler 메서드`가 같은 기능을 제공한다.
