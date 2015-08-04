# 규칙 59. 불필요한 점검지정 예외 사용은 피하라

`점검지정 예외`을 `무점검지정 예외`로 바꿀 수 있는 방법을 고민해봐라.
`예` 예외를 던지는 메서드를 둘로 나눠서 첫번째 메서드가 boolean값을 반환하도록 만듬

```java
try{
    obj.action(args);
}catch(TheCheckedException e){
    // 예외상황 처리
}
```
