# 규칙 77. 개체 통제가 필요하다면 readResolve 대신 enum 자료형을 이용하라

```java
public class Elvis{
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }

    public void leaveTheBuilding(){ ... }
}
```

위와 같은 싱글턴 선언부에 `implements Serializable`을 붙이는 순간 더이상 싱글턴 클래스가 아니다. 왜냐하면 `readOjbect`메서드는 새로 생성된 객체를 반환하기때문에, 초기화 당시 만들어진 객체와 다르기 때문이다.
이때 `readResolve`를 이용하면 역직렬화가 끝나서 만들어진 객체에 대해 이 메서드가 호출됨으로, `readOjbect`가 만들어낸 객체를 다른것으로 대체할 수 있다.

```java
private Object readResolve(){
    // 싱글턴으로 만들어진 Elvis객체를 반환
    // 새로 만들어진 객체는 바로 GC가 처리
    return INSTANCE;
}
```

개체 통제를 위해 `readResolve` 활용할 때는 객체 참조 자료형으로 선언된 모든 객체 필드를 반드시 transient로 선언해야한다. 그렇지 않으면 76에서 설명한 `MutablePeriod`공격과 거의 비슷한 기술을 사용해서 참조를 가로챌 수 있게 된다.
(싱글턴 객체에 비-transient 필드가 들어있는경우, 해당 필드의 내용을 readResolve메서드 실행 전에 역직렬화 하여 readOjbect가 반환하는 원래 객체의 참조를 훔쳐낼 수 있다)
동작원리를 다시 살펴보면...
1. 도둑이 `숨을`직렬화된 싱글턴 객체를 참조하는 객체 필드와  `readResolve`메서드를 갖춘 `도둑(stealer)`클래스를 만든다
2. 직렬화 스트림안에 싱글턴의 `비-transient` 필드가 참조하는 대상을 도둑 객체로 바꿔놓는다
3. 싱글턴에 도둑 객체가 포함되고 도둑 객체는 싱글턴을 참조한다
4. 싱글턴에 도둑 객체가 포함되었으므로, 싱글턴이 역으로 직렬화될 때도 도둑 객체의 `readResolve`메서드가 먼저 실행된다
5. 그 결과 도둑 객체의 `readResolve` 메서드가 실행될 때 그 객체 필드는 여전히 부분적으로 역직렬화 된(그리고 아직 readResolve가 실행되지 않은)싱글턴 객체를 참조한다
6. 도둑 객체의 `readResolve` 메서드는 그 객체 필드에 보관된 참조를 정적 필드에 복사한다. (`readResolve`메서드가 실행된 후 객체를 참조할 수 있도록 하기 위해)
7. 이 메서드는 도둑 객체를 숨겼던 `비-transient`필드의 원래 자료형에 맞는 값을 반환한다
8. 그렇지 않으면 VM은 직렬화 시스템이 해당 필드에 도둑 객체를 저장하려는 순간에 ClassCastException을 발생시킬것이다

예제를 보고 이해하도록 노력해보자

```java
public class Elvis implements Serializable{
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { }

    private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
    private Object readResolve(){
        return INSTANCE;
    }
}


// 도둑 클래스
public class ElvisStealer implements Serializable{
    public static Elvis impersonator;
    private Elvis payload;

    private Object readResolve(){
      //  아직 "resovle"되지 않은 Elvis 객체의 참조 저장
        impersonator = payload;

        // favoriteSongs필드의 자료형에 맞는 객체 반환
        return new String[] { "A Fool Such as I" };
    }
    private static final long serialVersionUID = 0;
}


public class ElvisImpersonator{
  private static final byte[] serializaedForm = new byte[] {
        (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x06,
        ...
  }

  public static void main(String[] args){
      // ElvisStealer.impersonator를 초기화하고, 진짜 Elvis객체 Elvis.INSTANCE를 반환
      Elvis elvis = (Elvis) deserialize(serializaedForm);
      Elvis impersonator = ElvisStealer.impersonator;

      elvis.printFavorites();
      impersonator.printFavorites();
  }
}
```

위 프로그램을 실행하면 다음과 같이 출력된다. 두개의 `Elvis`객체를 만드는것이 가능하다는것이다.

[Hound Dog, Heartbreak Hotel]
[A Fool Such as I]

`favoriteSongs` 필드를 `transient`로 고쳐서 해결할 수 도 있지만, `Elvis`를 원소가 하나인 `enum`으로 고쳐서 해결하는 편이 더 낫다.

`ElvisStealer`공격을 통해 알 수 있듯이 `readResolve`를 이용한 방법은 깨지기도 하므로, 직렬화가 가능한  `enum`을 통해 구현하면 선언된 상수 이외의 다른 객체가 존재할 수 없다는 확실한 보장이 생기므로 프로그래머가 신경쓸 부분이 사라진다.
`enum`으로 작성한 `Elvis`예제를 보자

```java
public enum Elvis{
    INSTANCE;
    private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites(){
        System.out.println(Arrays.toString(favoriteSongs));
    }
}
```

> 하지만, 컴파일 시점에 직렬화 가능 클래스의 객체 수를 통제해야 할 경우 enum을 사용할 수 없으므로, 아직 readResolve방식은 유효하다

다만, `readResolve` 메서드를 사용할때 접근권한이 매우 중요하므로, 클래스의 모든 객체 필드를 기본자료형으로 하거나 아니면 `transient`로 선언해야한다.
