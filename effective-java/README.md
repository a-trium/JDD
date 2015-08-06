# Effective Java

## Chapter 2.  객체의 생성과 삭제

- [Rule 01](https://github.com/SKP4/JDD/blob/master/effective-java/Rule01.md) 생성자 대신 정적 팩토리 메소드를 이용하는 경우
- [Rule 02](https://github.com/SKP4/JDD/blob/master/effective-java/Rule02.md) 생성자 인자가 많거나, 선택적일 경우 빌더 패턴을 이용하자
- [Rule 03](https://github.com/SKP4/JDD/blob/master/effective-java/Rule03.md) private 생성자나 enum 자료형은 싱글턴 패턴을 따르도록 설계하자
- [Rule 04](https://github.com/SKP4/JDD/blob/master/effective-java/Rule04.md) 객체 생성을 막을 때는 private 생성자를 이용하자
- [Rule 05](https://github.com/SKP4/JDD/blob/master/effective-java/Rule05.md) 불필요한 객체는 만들지 말자
- [Rule 06](https://github.com/SKP4/JDD/blob/master/effective-java/Rule06.md) 유효기간이 지난 객체는 폐기하자
- [Rule 07](https://github.com/SKP4/JDD/blob/master/effective-java/Rule07.md) finalizer 사용을 피하자

## Chapter 3.  모든 객체의 공통 메서드

- [Rule 08](https://github.com/SKP4/JDD/blob/master/effective-java/Rule08.md) equals를 재정의할 때는 일반 규약을 따르라
- [Rule 09](https://github.com/SKP4/JDD/blob/master/effective-java/Rule09.md) equals를 재정의할 때는 반드시 hashCode도 재정의하라
- [Rule 10](https://github.com/SKP4/JDD/blob/master/effective-java/Rule10.md) toString은 항상 재정의하라
- [Rule 11](https://github.com/SKP4/JDD/blob/master/effective-java/Rule11.md) clone을 재정의할 때는 신중하라
- [Rule 12](https://github.com/SKP4/JDD/blob/master/effective-java/Rule12.md) Comparable 구현을 고려하라

## Chapter 4.  클래스와 인터페이스

- [Rule 13](https://github.com/SKP4/JDD/blob/master/effective-java/Rule13.md) 클래스와 멤버의 접근 권한을 최소화하라
- [Rule 14](https://github.com/SKP4/JDD/blob/master/effective-java/Rule14.md) public 클래스 안에는 public 필드를 두지 말고 접근자 메서드를 사용하라
- [Rule 15](https://github.com/SKP4/JDD/blob/master/effective-java/Rule15.md) 변경 가능성을 최소화 하라
- [Rule 16](https://github.com/SKP4/JDD/blob/master/effective-java/Rule16.md) 계승하는 대신 구성하라
- [Rule 17](https://github.com/SKP4/JDD/blob/master/effective-java/Rule17.md) 계승을 위한 설계와 문서를 갖추거나, 그럴 수 없다면 계승을 금지하라
- [Rule 18](https://github.com/SKP4/JDD/blob/master/effective-java/Rule18.md) 추상 클래스 대신 인터페이스를 사용하라
- [Rule 19](https://github.com/SKP4/JDD/blob/master/effective-java/Rule19.md) 인터페이스는 자료형을 정의할 때만 사용하라
- [Rule 20](https://github.com/SKP4/JDD/blob/master/effective-java/Rule20.md) 태그 달린 클래스 대신 클래스 계층을 활용하라
- [Rule 21](https://github.com/SKP4/JDD/blob/master/effective-java/Rule21.md) 전략을 표현하고 싶을 때는 함수 객체를 사용하라
- [Rule 22](https://github.com/SKP4/JDD/blob/master/effective-java/Rule22.md) 멤버 클래스는 가능하면 static으로 선언하라

## Chapter 5.  제네릭

- [Rule 23](https://github.com/SKP4/JDD/blob/master/effective-java/Rule23.md) 새 코드에는 무인자 제네릭 자료형을 사용하지 마라
- [Rule 24](https://github.com/SKP4/JDD/blob/master/effective-java/Rule24.md) 무점검 경고(unchcked warning)를 제거하라
- [Rule 25](https://github.com/SKP4/JDD/blob/master/effective-java/Rule25.md) 배열 대신 리스트를 써라
- [Rule 26](https://github.com/SKP4/JDD/blob/master/effective-java/Rule26.md) 가능하면 제네릭 자료형으로 만들 것
- [Rule 27](https://github.com/SKP4/JDD/blob/master/effective-java/Rule27.md) 가능하면 제네릭 메서드로 만들 것
- [Rule 28](https://github.com/SKP4/JDD/blob/master/effective-java/Rule28.md) 한정적 와일드카드를 써서 API 유연성을 높여라
- [Rule 29](https://github.com/SKP4/JDD/blob/master/effective-java/Rule29.md) 형 안전 다형성 컨테이너를 쓰면 어떨지 따져보라

## Chapter 6.  열거형(enum)과 어노테이션

- [Rule 30](https://github.com/SKP4/JDD/blob/master/effective-java/Rule30.md) int 상수대신 enum을 사용하라
- [Rule 31](https://github.com/SKP4/JDD/blob/master/effective-java/Rule31.md) ordinal 대신 객체 필드를 사용하라
- [Rule 32](https://github.com/SKP4/JDD/blob/master/effective-java/Rule32.md) 비트 필드(bit field)대신 EnumSet을 사용하라
- [Rule 33](https://github.com/SKP4/JDD/blob/master/effective-java/Rule33.md) ordinal을 배열 첨자로 사용하는 대신 EnumMap을 이용하라
- [Rule 34](https://github.com/SKP4/JDD/blob/master/effective-java/Rule34.md) 확장 가능한 enum을 만들어야 한다면 인터페이스를 이용하라
- [Rule 35](https://github.com/SKP4/JDD/blob/master/effective-java/Rule35.md) 작명 패턴 대신 어노테이션을 사용하라
- [Rule 36](https://github.com/SKP4/JDD/blob/master/effective-java/Rule36.md) Override 어노테이션은 일관되게 사용하라
- [Rule 37](https://github.com/SKP4/JDD/blob/master/effective-java/Rule37.md) 자료형을 정의할 때 표식 인터페이스를 사용하라

## Chapter 7.  메서드

- [Rule 38](https://github.com/SKP4/JDD/blob/master/effective-java/Rule38.md) 인자의 유효성을 검사하라
- [Rule 39](https://github.com/SKP4/JDD/blob/master/effective-java/Rule39.md) 필요하다면 방어적 복사본을 만들라
- [Rule 40](https://github.com/SKP4/JDD/blob/master/effective-java/Rule40.md) 메서드 시그니처는 신중하게 설계하라
- [Rule 41](https://github.com/SKP4/JDD/blob/master/effective-java/Rule41.md) 오버로딩할 때는 주의하라
- [Rule 42](https://github.com/SKP4/JDD/blob/master/effective-java/Rule42.md) varargs는 신중히 사용하라
- [Rule 43](https://github.com/SKP4/JDD/blob/master/effective-java/Rule43.md) null 대신 빈 배열이나 컬렉션을 반환하라
- [Rule 44](https://github.com/SKP4/JDD/blob/master/effective-java/Rule44.md) 모든 API 요소에 문서화 주석을 달라

## Chapter 9.  예외

- [Rule 57](https://github.com/SKP4/JDD/blob/master/effective-java/Rule57.md) 예외는 예외적 상황에만 사용하라
- [Rule 58](https://github.com/SKP4/JDD/blob/master/effective-java/Rule58.md) 복구 가능 상태에는 점검지정 예외를 사용하고, 프로그래밍 오류에는 실행시점 예외를 이용하라
- [Rule 59](https://github.com/SKP4/JDD/blob/master/effective-java/Rule59.md) 불필요한 점검지정 예외 사용은 피하라
- [Rule 60](https://github.com/SKP4/JDD/blob/master/effective-java/Rule60.md) 표준 예외를 사용하라
- [Rule 61](https://github.com/SKP4/JDD/blob/master/effective-java/Rule61.md) 추상화 수준에 맞는 예외를 던져라
- [Rule 62](https://github.com/SKP4/JDD/blob/master/effective-java/Rule62.md) 메서드에서 던져지는 모든 예외에 대해 문서를 남겨라
- [Rule 63](https://github.com/SKP4/JDD/blob/master/effective-java/Rule63.md) 어떤 오류인지를 드러내는 정보를 상세한 메시지에 담으라
- [Rule 64](https://github.com/SKP4/JDD/blob/master/effective-java/Rule64.md) 실패 원자성 달성을 위해 노력하라
- [Rule 65](https://github.com/SKP4/JDD/blob/master/effective-java/Rule65.md) 예외를 무시하지 마라

... Coming soon!
