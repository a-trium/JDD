# Rule 63 - 어떤 오류인지 드러내는 정보를 상세한 메시지에 담으라

## 오류의 상세 메세지에 예외와 관련된 모든 인자와 필드값을 포함시켜야 한다.

예를 들어 `IndexOutOfBounds` Exception 의 예외 메세지에는 첨자의 하한, 상한, 그 범위를 벗어난 첨자값이 모두 포함되야 한다.
 
**오류 정보를 상세하게 남기는 한 가지 방법은, 상세한 정보를 요구하는 생성자를 만드는 것이다.**

```java
public IndexOutOfBoundsException(int lowerBound, int upperBound, int index) {
    ...
    ...
}
```

**자바 플랫폼 라이브러리는 이 숙어를 많이 활용하지 않지만, 높이 추천할 만한 기법이다.**