# 규칙 58. 복구가능 상태에는 점검지정 예외를 사용하고, 프로그래밍 오류에는 실행지점 예외를 이용하라.

자바의 `throwable`
- 점검지정 예외(checked exception)
- 실행시점 예외(runtime exception)
- 오류(error)

>호출자(caller)측에서 복구할것으로 여겨지는 상황에 대해서는 checked exception을 이용해야한다

`checked exception` 던지는 메서드를 호출한 클라이언트는 해당 예외를 `catch`절 안에서, 혹은 밖으로 던져지도록 놔두든지 해야 한다. API사용자에게 `checked exception` 예외를 준다는 것은, 해당 상태를 복구할 권한을 준다는 뜻이다.


**복구가능 상태** : checked exception  
**오류를 나타낼때** : runtime exception
