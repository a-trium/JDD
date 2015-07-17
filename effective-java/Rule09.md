# 규칙09. equals를 재정의할 때는 반드시 hashCode도 재정의하라

`equals`메서드를 재정의하는 클래스는 반드시 `hashCode`메서드를 재정의 해야함, 많은 버그가 `hashCode`메서드를 재정의하지 않아서 발생  

[JavaSE6] `Object` 클래스 명세
- `hashCode`메서드를 여러번 호출하는경우, `equals`가 사용하는 정보들이 변경되지 않았다면, 언제나 동일한 **정수** 가 반환되어야 함. 단, 프로그램 재시작시 같은값이 나올 필요는 없음
- `equals(Obejct)`메서드가 같다고 판정한 두 객체의 `hashCode`값은 같아야 함
- `equals(Object)`메서드가 다르다고 판정한 두 객체의 경우 `hashCode`값이 같지 않아도 됨. 그러나 서로 다른 `hashCode`값이 나오면 `해시 테이블(hash table)`성능이 향상될 수 있다는점은 이해하고 있어야 함

</br>

`hashCode`를 재정의하지 않으면 두번째 핵심규약이 위반됨.  

```java
Map<PhoneNumber, String> map = new HashMap<>();
map.put(new PhoneNumber(707, 876, 5309), "Jenny");
```

위와같이 해놓고 `m.get(new PhoneNumber(707, 876, 5309))`를 호출하면 `Jenny`가 아닌 `null`이 반환된다. new연산을 통해 두개의 서로 다른 객체가 선언된 것이다.  
서로다른 `hashCode`을 가지고 있으므로, `get`메서드는 `put`메서드가 객체를 저장한 것과다른 `해시 버킷(hash bucket)`에서 해당 객체를 찾게된다. **운이 좋아 같은 버킷을 찾게되어도 get은 항상 거의 null을 반환할 것이다**

```java
@Override
public int hashCode() { return 42; }
```
> 그렇다고 위와 같이 `hashCode`값을 모두 같은 값을 지니게 하면 전부 같은 버킷에 담기게 됨으로, 해시테이블이 연결리스트가 되어버린다.

</br>
## 이상적인 해시함수에 가깝게 만드는 지침
1. 0이 아닌 상수를 `result`라는 이름의 `int변수`에 저장
2. 객체 안에 있는 모든 중요 필드 `f`에 대해 아래 절차를 시행  
  - A. 해당 필드에 대한 int해시 코드 c를 계산
    - 필드가 `boolean`  ->  `(f ? 1 : 0)`을 계산
    - 필드가 `byte`, `char`, `short`, `int`중 하나  ->   `(int) f`계산
    - 필드가 `long`    ->  `(int)(f^(f>>>32))`를 계산
    - 필드가 `float`   ->  `Float.floatToIntBits(f)`
    - 필드가 'double'  ->  `Double.doubleToLongBits(f)를 계산하고, 그 결과로 얻은 `long`값을 3번째 `long`절차에 따라 진행`
    - 필드가 `객체 참조`이고 `equals`메서드가 해당 필드의 `equals`메서드를 재귀적으로 호출하는 경우  ->  해당 필드의 `hashCode`메서드를 재귀적으로 호출하여 해시코드 계산  
    복잡한 비교가 필요한 경우, 해당 필드의 **대표 형태(canoical representation)** 를 계사한 다음, 대표 형태에 대해 `hashCode`를 호출
    - 필드가 `array`   ->  각 원소가 별도 필드인 것처럼 계산  
  - B. A에서 계산된 해시 코드 `c`를 `result`에서 다음과 같이 결합
```java
result = 31 * result + c;
```
3. `result`를 반환
4. `hashCode`구현이 끝났다면, 동치 관계에 있는 객체의 해시 코드 값이 똑같이 계산되는지 점검
