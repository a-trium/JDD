규칙4. 객체 생성을 막을 때는 private 생성자를 사용하라.

  1. static 메서드/필드만 모은 클래스를 만들때
  2. 특정 인터페이스를 구현하는 객체를 만들때
  3. final 클래스에 적용할 메서드들을 모아놓을 때

  자바 컴파일러는 생성자를 생략하면 인자없는 default constructor를 만들어 버리기 때문에
  이러한 유틸리티 클래스(utility class)들은 객체를 만들 목적이 아니므로 생성자를 private 으로 만들어야한다.

    public class UtilityClass(){
        private UtilityClass() {

            // 실수 방지를 위한 익셉션
            throw new AssertionError();
        }
        ... // 생략
    }

   4. 하위 클래스도 만들 수 없다
      모든 생성자는 상위 클래스의 생성자를 명시적이든 묵시적이든 호출할 수 있어야 하지만, 호출 가능한 생성자가 없기 때문에 하위 클래스 생성 불가능
