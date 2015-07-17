# 규칙08. equals를 재정의할 때는 일반 규약을 따르라

`equals`메서드 -> 객체가 자기 자신과 같은지 확인할 때 사용</br></br>
다음 중 하나라도 만족하면 된다
- 각각의 객체가 고유하다  
- 클래스에 **논리적 동일성(logical equality)** 검사 방법이 있건 없건 상관없다  
- 상위 클래스에서 재정의한 `equals`가 하위 클래스에서 사용하기에도 적당하다  
- 클래스가 `private`또는 `package-private`로 선언되었고, `equals`메서드를 호출 할 일이 없다
</br>

`Object.equals`의 재정의가 필요할때는?
- **객체 동일성(object equality)** 이 아닌 **논리적 동일성(local equality)** 의 개념을 지원하는 클래스일때  
- 상위 클래스의 `equals`가 하위 클래스의 필요를 충족하지 못할 때  
</br>

`Integer`나 `Date`처럼, 어떤 값을 표현하는 `값 클래스(value class)` 는 두 객체가 같은 값을 나타내는지 확인하기 위해 `equals` 를 사용하지, 동일한 객체인지 확인하기 위해 호출하지 않는다!  
또한, `열거 자료형(enum)` 은, **규칙01.** 기능을 사용해 단 하나의 객체만 존재하도록 제한하는데, 이런 클래스에서는 **(객체동일성 = 논리적 동일성)** 이다. 따라서 `Obejct` 에 정의된 `equals` 메서드만 사용해도 논리적 동일성을 검사할 수 있다.  
</br>

`equals` 메서드의 **동치 관계(equivalence relation) 규칙**
- 반사성(reflexive): null이 아닌 참조x가 있을때, x.equals(x)는 true를 반환
- 대칭성(symmetric): null이 아닌 참조 x와 y가 있을때, x.equals(y)는 y.equals(x)가 true일때만 true반환
- 추이성(transitive): null이 아닌 참조 x,y,z가 있을때, x.equals(y)가 true이고 y.equals(z)가 true이면, x.equals(z)도 true
- 일관성(consistent): null이 아닌 참조 x와 y가 있을떄, equals를 통해 비교되는 정보에 아무 변화가 없다면, x.equals(y) 호출 결과는 호출 횟수에 상관없이 항상 같아야 함
- null이 아닌 참조 x에 대해서, x.equals(null)은 항상 false


`리스코프 대체 원칙(Liskov substitution principle` : 어떤 자료형의 중요한 속성(property)은 하위 자료형에 그대로 유지되어, 그 자료형을 위한 메서드는 하위 자료형에서도 잘 동작해야 한다.
</br>
## equlas 메서드를 구현하기 위한 지침
1. `==` 연산자를 사용하여 `equals` 의 인자가 자기 자신인지 검사
2. `instanceof` 연사자를 사용하여 인자의 자료형이 정확한지 검사
3. `equals`의 인자를 정확한 자료형으로 변환
4. **중요** 필드 각각이 인자로 주어진 객체의 해당 필드와 일치하는지 검사
5. `equals` 메서드 구현을 끝냈다면, 대칭성/추이성/일관성 3가지 속성이 만족되는 검토
</br>

`equals`를 구현할때 주의해야 할 사항(추가)
- `hashCode`도 재정의하라
- 너무 머리쓰지 마라
- `equals`메서드의 인자형을 `Object`에서 다른것으로 바꾸지 마라
