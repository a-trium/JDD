# 규칙 76. readObject 메서드는 방어적으로 구현하라

```java
// 방어적 복사를 이용하는 변경 불가능 클래스
public final class Period{
  private final Date start;
  private final Date end;

  public Period(Date start, Date end){
      this.start = new Date(start.getTime());
      this.end = new Date(end.getTime());

      if(this.start.compareTo(this.end) > 0)
        throw new IllegalArgumentException(start + " after " + end);
  }

  public Date start() { return new Date(start.getTime()); }
  public Date end() { return new Date(end.getTime()); }
  public String toString() { return start + " - " + endl; }

  ... // 생략
}
```

위 클래스를 직렬화 가능 클래스로 만드는데 문제점
1. readObject 메서드가 `public 생성자`나 마찬가지
2. 생성자와 마찬가지로 인자의 유효성 검사 필요
3. 필요하다면 인자를 방어적으로 복사해야함

`readObject`는 `바이트 스트림(byte stream)`을 인자로 받는 생성자다.
문제는, 인공적으로 만들어진 바이트 스트림을 `readObject`에 인자로 넘길떄 생긴다. 아래 예제를 보자

```java
public class BogusPeriod{
    private static final byte[] serializaedForm = new byte[] {
          (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x06,
          ...
    }
    public static void main(String[] args){
        Period p = (Period) deserialize(serializaedForm);
        System.out.println(p);
    }

    // 지정된 직렬화 형식을 준수하는 객체
    private static Object deserialize(byte[] sf){
        try{
            InputStream is = new ByteArrayInputStream(sf);
            OjbectInputStream ois = new ObjectInputStream(is);
            return ois.readObject();
        } catch(Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
```

위 코드에 포함된 byte배열 `serializaedForm`을 이용하여 `Period`객체를 만든다. `Period`를 직렬화 가능한 클래스라고 선언하는 것 만으로는 클래스 불변식을 위반하는 객체 생성을 막을 수 없는 것이다.
때문에, `defaultReadOjbect` 메서드를 호출하는 `readOjbect`메서드를 `Period`클래스에 구현하여 역직렬화된 객체의 유효성을 검사해야한다. 아래 코드를 보자

```java
private void readObject(ObjectInputStream s) throw IOException, ClassNotFoundException{
    s.defaultReadOjbect();

    // 불변식 만족되는지 검사
    if(start.compareTo(end) > 0)
        throw new InvalidObjectException(start + " after " + end);
}
```

이렇게하면 공격자가 이상한 `Period`객체를 생성하는것을 막을 수 있으나, 유효한 `Period`객체로 시작하는 바이트 스트림을 만든다음, 객체 내부의 `private Date 필드`에 대해 `악의적인 객체 참조(rogue object reference)`하는것은 막을 수 없다. 다음 에제를 보자.

```java
public class MutablePeriod{
    public final Period period;
    public final Date start;
    public final Date end;

    public MutablePeriod(){
        try{
            ByteArrayInputStream bos = new ByteArrayInputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            // 유효한 Period 객체를 직렬화
            out.writeObject(new Period(new Date(), new Date()));

            byte[] ref = { 0x71, 0, 0x7e, 0, 5 };
            bos.write(ref);

            ref[4] = 4;
            bos.write(ref);

            ObjectInputStream in = new OjbectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            period  = (Period) in. readObject();
            start   = (Date) in. readObject();
            end     = (Date) in. readObject();
        }catch(Exception e){
            throw new AssertionError(e);
        }
    }
}

public static void main(String[] args){
    MutablePeriod mp = new MutablePeriod();
    Period p = mp.period;
    Date pEnd = mp.end;

    // 시간을 되돌리자
    pEnd.setYear(78);
    System.out.println(p);

    // 60년대로 돌아간다!
    pEnd.setYear(69);
    System.out.println(p);
}
```

불변식에 변화없는 `Period`객체를 만들었음에도, 내부 컴포넌트가 마음대로 바꿀 수 있게 됬다. 이렇게 만들어진 클래스는 시스템에 엄청난 해를 끼칠 수 있다. 이는 `Period`의 `readObject` 메서드가 방어적 복사를 충분히 하지 않아서 발생한 것이다.
> 때문에 객체를 역으로 직렬화할 때는 클라이언트가 가질 수 없어야 하는 객체 참조를 담은 모든 필드를 방어적 복사하도록 해야한다.

```java
private void readobject(ObjectInputStream s) throws IOException, ClassNotFoundException{
    s.defaultReadOjbect();

    // 방어적 복사
    start = new Date(start.getTime());
    end = new Date(end.getTime());

    // 불변식이 만족되는지 검사
    if(start.compareTo(end) > 0)
      throw new IllegalArgumentException(start + " after " + end);
}
```

위의 코드는 다음 두가지 사항을 따르고 있다.
1. 유효성 검사 이전에 방어적 복사를 시행하며, `clone`메서드를 사용하지 않는다
2. `final`로 선언된 필드는 방어적 복사를 할 수 없다 -> `final`키워드를 사용하면 안된다

릴리즈 `1.4` 이후로는 방어적 복사를 하지 않아도 악의적 객체 참조 공격을 막도록 고안된 `writeUnshared`와 `readUnshared`메서드가 `ObjectOutputStream`에 추가되었다. 하지만 규칙77에서 설명할 `ElvisStealer`공격에 취약하므로 사용하지 마라.

안전한 `readObject` 메서드를 구현하고자 할떄 따라야 하는 지침들
1. private로 남아있어야 하는 객체 참조 필드를 가진 클래스는 그런 필드가 가리키는 객체를 방어적으로 복사해야 한다. 변경 불가능 클래스의 변경 가능 컴포넌트가 이 범주에 해당한다.
2. 불변식을 검사해서 위반된 사실이 감지될 경우 InvalidObjectException을 던져라. 불변식 검사는 방어적 복사 이후에 시행해야 한다.
3. 만일 객체를 완전히 역직렬화 한 다음에 전체 객체 그래프의 유효성을 검사해야 한다면, ObjectInputValidation 인터페이스를 이용하라
4. 직접적이건 간접적이건, 재정의 가능 메서드를 호출하지 마라.
