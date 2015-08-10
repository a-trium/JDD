# Rule 75 - 사용자 지정 직렬화 형식을 사용하면 어떨지 살펴보라

일반적으로 사용자 정의 직렬화 형식을 설계한다면, 이용하게 될 인코딩 방식과 대부분 동일한 경우에만 기본 직렬화 형식을 사용해야 한다.

어떤 객체의 기본 직렬화 형식은 해당 객체가 루트(*root*) 인 객체 그래프의 물리적표현을 나름 효과적으로 인코딩 한 것이다. 다시 말해 객체 안에 담긴 데이터와 
해당 객체를 통해 접근할 수 있는 모든 객체에 담긴 데이터를 기술한다. 또한 이 객체들이 서로 연결된 토폴로지도 기술한다. 

그런데 어떤 객체의 직렬화 형식은 해당 객체가 나타내는 **논리적** 데이터만 담아야 하며 물리적인 표현과는 무관해야 한다. 

> **기본 직렬화 형식은 그 객체의 물리적 표현이 논리적 내용과 동일한때만 적절하다**

## 기본 직렬화 형식이 적절한 경우
 
```java
// Good candidate for default serialized form
public class Name implements Serializable {

/**
* Last name. Must be non-null.
* @serial
*/

private final String lastName;

/**
* First name. Must be non-null.
* @serial
*/

private final String firstName;

   

/**
* Middle name, or null if there is none.
* @serial
*/

private final String middleName;
... // Remainder omitted
}  
```

<br/>

**설사 기본 직렬화 형식이 만족스럽다 하더라도, 불변식이나 보안 조건을 만족시키기 위해서는 readObject 메서드를 구현해야 한다.** `Name` 의 경우 `readObject` 메서드는 `lastName`, `firstName` 이 `null` 이 되지 않는 조건을 만족시키도록 구현되어야 한다. 

위 클래스에서 각 필드는 `private` 임에도 주석이 달려있는데, 이는 이 필드들이 `public` API 인 클래스의 직렬화 형식을 규정하기 때문이다. 반드시 문서화 해야 한다.

## 기본 직렬화 형식이 적절하지 않은 경우

```java
// Awful candidate for default serialized form

public final class StringList implements Serializable {

    private int size = 0;

    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }

... // Remainder omitted
}
```

논리적으로 이 클래스는 문자열의 리스트를 표현한다. 물리적으로 보자면 이 리스트는 이중 연결 리스트다. **기본 직렬화를 사용할 경우 모든 연결리스트 항목과 항목간 양방향 연결 구조가 직렬화 형식에 그대로 반영될 것이다.** 
객체의 물리적 표현형태가 논리적 내용과 많이 다른데도, 기본 직렬화를 사용할 경우 다음 네 가지 문제가 생길 수 있다.

- **공개 API가 현재 내부 표현상태에 영원히 종속된다.** 위 예제에서 `private` `StringList.Entry` 클래스는 `public` API가 되어버린다. 다른 내부 표현 방식을 채택하더라도, `StringList` 클래스는 여전히 연결리스트 표현을 입력으로 사용해서 객체를 생성할 수 있어야 할 것이다. 
그러니 연결 리스트 항목 처리에 관련된 코드를 제거하는 것은 영원히 불가능하다. 설사 더 이상은 연결 리스트를 사용하지 않는다 해도
- **너무 많은 공간을 차지할 수 있다.** 위 예제의 기본 직렬화 형식은 리스트 항목 사이의 모든 연결 정보가 쓸데없이 들어간다. 이런 정보는 구현 세부사항에 해당하는 것으로, 
직렬화 형식에 포함시킬 가치가 없다. 직렬화 결과도 쓸데없이 커진다.
- **너무 많은 시간을 소비할 수 있다.** 기본 직렬화 로직은 객체 그래프 토폴로지 정보를 이해하지 못하므로, 많은 양의 그래프 ㄹㅐ프 순회를 해야한다.
- **스택오버플로우 문제가 생길 수 있다.** 기본 직렬화 절차는 재귀적인 그래프 순회를 해야하는데, 설사 그래프 크기가 과도한 수준이 아니라 해도 스택오버플로우가 발생할 수 있다.

`StringEntry` 리스트의 적절한 직렬화 형식은 문자열 수, 그리고 실제 문자열들의 나열일 것이다. 논리적 데이터 형태만을 나타내고, 물리적 세부사항은 제거된 것이다. 

```java
public class StringList implements Serializable {
    private static final long serialVersionUID = 7533856777949584383L;
    private transient int size = 0;
    private transient Entry head = null;
    private transient Entry tail = null;
    
    // No longer Serializable!
    private static class Entry {
        String data;
        Entry next;
        @SuppressWarnings("unused")
        Entry previous;
    }
    
    // Appends the specified string to the list
    public final void add(String s) {
        Entry e = new Entry();
        e.data = s;
        
        if (null == head) {
            tail = head = e;
        } else {
            tail.next = e;
            tail.next.previous = tail;
            tail = tail.next;
        }
        
        size++;
        
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);
        // Write out all elements in the proper order.
        for (Entry e = head; e != null; e = e.next)
        s.writeObject(e.data);
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numElements = s.readInt();
        // Read in all elements and insert them in list
        for (int i = 0; i < numElements; i++)
        add((String) s.readObject());
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry it = head; it != null; it = it.next)
        sb.append(it.data);
        return sb.toString();
    }
    
    public static void main(String[] args) {
        
        StringList sl = new StringList();
        
        for (int i = 0; i < 5; i++) {
            sl.add(String.valueOf(i));
        }
        
        System.out.println(sl);
        
        try {
            FileOutputStream fos = new FileOutputStream("t.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sl);
            oos.close();
            FileInputStream fis = new FileInputStream("t.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            StringList sl2 = (StringList) ois.readObject();
            System.out.println("Desialized obj = " + sl2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // Remainder omitted
}
```

`writeObject` 가 맨 처음으로 `defaultWriteObject` 를 호출하고 있음에 주의하자. 모든 필드가 *transient* 임에도 불구하고. 

> 객체의 모든 필드가 *transient* 일 때는 `defaultWriteObject` 나 `defaultReadObject` 를 호출하지 않는 것도 가능하긴 하지만 권장하는 사항은 아니다. 

설마 모든 필드가 `transient` 이더라도, `defaultWriteObject` 를 호출하면 직렬화 형식이 바뀌며, 그 결과로 유연성이 크게 향상된다. 나중에 *non-transient* 객체 필드를 호출하더라도 
하위 호환성이 유지된다. 나중 버전 소프트웨어에서 직렬화한 객체를 이전 버전에서 역으로 직렬화 할 때, 새로 추가한 필드는 무시된다. 물론 이전 버전의 `readObject` 안에서 `defaultReadObject` 호출을 빼먹으면 역 직렬화 과정은 `StreamCorruptedException` 을 내면서 중단된다. 

<br/>

`StringList` 의 기본 직렬화 형식은 성능도 나쁘고, 유연성도 떨어지지만 원래 객체의 충실한 사본이다. 그러나 특정 구현 세부사항에 종속된 불변식이 있는 객체라면, **그렇지 않다.** 예를 들어 
해시테이블의 경우 해시 테이블은 물리적으로 키-값 쌍들이 들어있는 해시 버킷이 죽 나열된 것이다. 어떤 쌍이 어떤 버킷에 들어있는가는 해시 코드에 따라 결정되는데, 이 코드는 일반적으로 JVM 구현마다 달라질 수 있다. 
따라서 해시테이블의 기본 직렬화 형식을 쓰면, 심각한 버그가 생긴다. 직렬화 후 역직렬화하면, 불변식이 심각하게 위배된 객체를 얻을 수 있다.




