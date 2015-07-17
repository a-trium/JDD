#규칙 12 Comparable구현을 고려하라
comopareTo 메서드는 Object에 선언되어있지 않다.
이 메서드는 Comparable인터페이스에 포함된 유일한 메서드이다.
Comparable 인터페이스를 구현하는 클래스의 객체들은 자연적순서(naturla ordering)를 갖게된다.
Comparable을 구현한 객체들의 배열을 정렬하는 것 : Arrays.sort(a);

아래의 코드 : String이 Comparable을 구현하고 있다는 사실을 이용해 명령행(command-line)인자들을 알파벳 순서로 정렬하는 동시에 중복 제거

public WordList {
	public static void main(String[] args){
		Set<String> s = new TreeSet<String>();
		Collections.addAll(s, args);
		System.out.println(s);
	}
}

자바 플랫폼 라이브러리이ㅔ 포함된 거의 모든값 클래스(value class)는 Comparable인터페이스를 구현
compareTo메서드의 일반규칙은 equals와 비슷

+ compareTo를 구현하는 경우
- 모든 x, y에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) (y.compareTo(x))가 예외를 발생시킨다면, (x.compareTo(y))도 그래야 하고, 그 역도 성립해야 함
- 추이성(transitivity)이 만족되도록 해야 함
(x.comparTo(y) > 0 && y.compareTo(z) > 0)이면 x.compareTo(z) > 0 이어야 함
- x.compareTo(y) == 0이면 sgn(x.compareTo(z)) == sgn(t.compareTo(z))의 관계가 모든 z에 대해 성립하도록 해야 함

- (x.comopareTo(y) == 0) == (x.equals(y))이다. -> 필수는 아님!
											-> 만족하지 않는 클래스는 반드시 그 사실을 명시해야 함 ex) 주의 : 이 클래스의 객체들은 equls에 부합하지 않는 자연적 순서르 따른다.


equals와는 달리 compareTo는 서로 다른 클래스 객체에는 적용될 필요가 없다.
compareTo규약을 준수하지 않는 클래스는 비교연산에 기반한 클래스들을 오동작시킬 수 있다.
comparable인터페이스는 구현하는 클래스에 값 요소를 추가하고 싶을 때는 원래 클래스를 계승하여 확장하는 대신, 원래 클래스 객체를 필드로 포함하는 새로운 클래스를 만들고, 원래 클래스의 객체를 반환하는 뷰(view)메서드를 추가한다.


권고사항 : compareTo를 통한 동치성 검사 결과는 equals메서드 실행결과와 같아야 함
compareTo메서드의 필드 비교 방식은 동치성 검사라기보다는 순서 비교다.
