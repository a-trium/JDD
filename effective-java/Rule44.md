# 규칙 44. 모든 API요소에 문서화 주석을 달라

좋은 API문서를 만드려면 API에 포함된 모든 `클래스`, `인터페이스`, `생성자`, `메서드`, `필드 선언`에 문서화 주석을 달아야 한다.
또한, `메서드` 문서화 주석은 메서드와 클라이언트 사이의 규약을 간단히 설명해야하고고, 계승을 위해 설계된 메서드가 아니라면 무엇을 하는지 설명해야지 어떻게 그 일을 하는지 설명하면 안된다.

- 문서화 주석의 첫번째 `문장`은, 해당 주석에 담긴 내용을 요약한것 이다.(summary description)
- javadoc으로 처리할때 요약문에 마침표가 여러번 포함되는 경우 주의해라(요약문이 잘릴 수 있기때문에)
- 제네릭 자료형이나 메서드에 주석을 달 때는 모든 자료형 인자들을 설명해야 한다
- enum 자료형에 주석을 달 때는, 자료형이나 public 메서드 뿐 아니라 각각의 상수에도 주석을 달아야 한다
- 어노테이션 자료형에 주석을 달때는 자료형뿐 아니라 모든 멤버에도 주석을 달아야 한다

> 문서화 주석에 오류를 줄이는 간단한 방법은, javadoc 실행결과 만들어진 HTML파일을 HTML 유효성 도구(validity checker) 로 검사하는 것이다.
