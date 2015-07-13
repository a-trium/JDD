규칙3. private 생성자나 enum 자료형은 싱글턴 패턴을 따르도록 설계하라

싱글턴 사용시 주의사항
  - 리플렉션을 통해 private 생성자를 호출 할 수 있으므로, 두번째 객체를 생성하라는 요청을 받으면 예외를 던져야 한다.
  - 예제 25page 하단

[JDK 1.5 이전] 싱글턴을 만드는 두가지 방법
  <공통> 생성자는 private으로 선언하고, 싱글턴 객체는 static 멤버를 통해 이용한다
  1. 정적 멤버는 final로 선언한다
    - 이 멤버가  public static final로 되어 있음

  2. pulbic으로 선언된 정적 팩토리 메서드를 이용한다
    - 멤버 변수는 private static final 이지만, getInstance() 함수를 통해 접근하도록 되어있음

[JDK 1.5 이후] 싱글턴을 만드는 새로운 방법
  원소가 하나뿐인 enum 자료형을 정의하는 것

    public enum Elvis{
        Instance;

        public void leaveTheBuilding() { ... }
    }

  1. 장점
    - 직렬화 처리가 자동으로 된다.
    - 리플렉션을 통한 공격에 안전하다
