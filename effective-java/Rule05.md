규칙5. 불필요한 객체는 만들지 마라

기능적으로 동일한객체 -> 만드는것보다 재사용이 낫다
객체를 재사용하는 프로그램은 더 빠르고 우아하며, 변경 불가능(immutable)객체는 언제나 재사용할 수 있다.

    절대로 피해야 하는 극단적인 예
    String s = new String("stringette");

위 문장은 실행될때마다 String 객체를 생성하므로

    String s = "stringette";

위와같이 사용하면 동일한 String객체를 이용한다. VM에서 해당 객체를 재사용 하게 됨

추가적인 예제는 책 30~31page Date객체 재활용 참고

[JDK 1.5 이후] 쓰데없이 객체 생성 방법이 추가 (autoboxing)

    // 무시무시할 정도로 느린 프로그램. 어디서 객체가 생성되었는지 생각해볼것
    public static void main(String[] args){
        Long sum = 0L;
        for(long i=0; i<Integer.MAX_VALUE; i++){
            sum += i;
        }
    }
위 코드에서 long 타입의 i가 Long 타입의 sum과 더해질때 autoboxing이 이루어지며 새롭게 객체가 생성(2^31개)
때문에, 객체 표현형 대신 기본 자료형을 사용하고, 생각지도 못한 autoboxing이 발생하지 않도록 유의해야 한다
// 객체 생성 비용이 높으니 무조건 피하라는 의미가 아님을 주의할것!

직접 관리하는 객체풀(object pool)을 만들어 객체 생성을 피하는 기법은 객체 생성 비용이 극단적으로 높지 않으면 사용하지 않는것이 좋다.
객체 풀을 만드는 비용이 정당화 될만한 예 : DB연결, Socket연결 등
