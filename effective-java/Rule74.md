# Rule 74 - Serializable 인터페이스를 구현하 때는 신중하라

## 일단 클래스를 릴리즈하고 나면 클래스의 구현을 유연하게 바꾸기 어려워진다 

`Serializable` 을 구현하면 **클래스의 바이트스트림 인코딩** 도 공개 API 의 일부가 되어 버린다. 널리 배포된 클래스의 직렬화의 형식은 일반적으로는 **영원히 지원해야 한다.**

**사용자 정의 직렬화 형식을 설계하지 않고 기본 직렬화 형식(default) 를 그대로 이용할 경우, 직렬화 형식은 영원히 클래스의 내부 표현방식에 종속된다.** 
다시 말해서 기본 직렬화 형식을 받아들이면 그 클래스의 *private* 와 *package-private* 객체 필드도 공개 API 가 된다는 것이다.
 
기본 직렬화 형식을 받아들인 상태에서, 클래스의 내부 표현을 변경하면 직렬화 형식에 호환 불가능한 변화가 생길 수 있다. 옛 버전 클래스로 만든 객체를 직렬화 하고, 
새 클래스로 역직렬화 하면 오류가 발생할 것이다. `ObjectOutputStream.putField`, `ObjectInputStream.readField` 를 이용해 원래 직렬화 형식을 유지하면서도 내부 표현을 바꿀 수 있긴 하지만 
까다로울뿐 아니라 소스코드에도 흔적이 남는다.

> 오랫동안 사용할 수 있는 고품질 직렬화 형식을 주의깊게 설계해야 한다. 초기 개발비용은 증가하겠지만, 그럴만한 가치는 충분하다.

### Serial Version UID

모든 직렬화 가능 클래스에 `serialVersionUID` 라는 이름의 `static final long` 필드를 명시적으로 선언하지 않으면 시스템은 복잡한 절차를 거쳐 자동으로 생성한다. 
자동 생성 식별번호는 아래의 나열된 정보들에 의해 영향을 받는다. 따라서 아래 정보중 하나라도 변경되면 새로운 식별번호가 생성된다.

- 클래스 이름
- 클래스가 구현하는 모든 인터페이스 이름
- 해당 클래스의 모든 `public`, `protected` 멤버

<br/>

## 2. 버그나 보안취약점이 발생할 가능성이 높아진다
 
**직렬화는 언어 외적인 객체 생성 메커니즘이다.** 일종의 *숨은 생성자* 로 볼 수 있다. 역직렬화 과정에 관계된 생성자가 명시적으로 존재하지 않기 때문에, 
생성자가 만족하는 모든 불변식을 보장해야 한다는 것, 그리고 생성중인 객체의 내부에 공격자가 접근할 수 없도록 해야한다는 것을 잊기 쉽다. 기본 역직렬화 메커니즘을 그대로 사용할 경우 다음의 문제에 쉽게 노출된다. 

- **불변식 훼손**
- **불법 접근**

<br/>

## 3. 새 버전 클래스를 내놓기 위한 테스트 부담이 늘어난다.

직렬화 가능 클래스를 수정할 때는, 새 릴리즈에서 만들고 직렬화한 객체를 예전 릴리즈에서 역 직렬화할 수 있는지 그리고 그 역도 가능한지 검사해야 한다.  

> 테스트 케이스 숫자: **직렬화 가능 클래스 X 릴리즈 수**

<br/>

## 계승을 염두에 두고 설계하는 클래스는 Serializable 을 구현하지 않는 것이 바람직하다. 인터페이스도 마찬가지

계승을 고려해 설계된 클래스 가운데 `Serializable` 을 구현하는 것으로는 다음의 클래스들이 있다.

- `Throwable`
- `Component`
- `HttpServlet`

`Throwable` 이 `Serializable` 을 구현한 것은 RMI 발생하는 예외를 전달하기 위함이다. `Component` 의 경우에는 `GUI` 를 전송 보관하기 위한 것이고, 
`HttpServlet` 은 세션 상태를 캐시하기 위해서 `Serializable` 을 구현 한다.

<br/>

[Rule 17 - 계승을 위한 문서를 갖추거나, 그럴 수 없다면 금지하라](https://github.com/SKP4/JDD/blob/master/effective-java/Rule17.md)

- `readObject` 또한 생성자처럼 동작하기 때문에 재정의 메서드를 호출하면 안된다. 만약 재정의 메서드를 호출하게 되면, 하위 클래스의 상태가 완전히 역직렬화 되기 전에 
해당 메소드가 호출된다.
- `readResolve` 와 `writeReplace` 가 있다면 반드시 `protected` 로 선언해야 한다. `private` 가 있을 경우 하위 클래스가 무시해 버릴 것이다.
- 객체 필드를 갖는 클래스를 직렬화 가능하고 계승 가능한 클래스로 구현할 때, **객체 필드가 기본값으로 초기화되면 위배되는 불변식이 있을 경우는 반드시 readObjectNoData** 를 추가해야 한다.

```java
private void radObjectNoData() throws InvalidObjectException {
    throw new InvalidObjectException("Stream data required");
}

class Person implements Serializable{
    private String name;
    private int age;

    Person() { this("default",1); }

    Person(String name, int y) {
        this.name = name;
        this.age = y;
    }
}

class Employee extends Person  {

    String address ;

    public Employee() {
        super();
        address ="default_address";
    }

    public Employee(String name , int age, String address) {
        super(name,age);
        this.address = address;
    }
}
```

> If Employee was serialized when it did not extend Person and later deserialized when it did then the Person part would be initialized to empty string and 0 age. Using this method, you can initialize them to "name" and 1 respectively.

`readObjectNodata` 메서드는 1.4 부터 지원된 메서드로 **기존 직렬화 가능 클래스에 새로운 직렬화 가능 클래스를 상위 클래스로 추가하는 드문 경우를 지원하기 위한 것이다.**

<br/>

## Serializable 을 구현하지 않기로 했을 경우 주의해야할 사항

상속을 고려해 설계하였으나 직렬화를 가능하지 않은 클래스에 대해서, **직렬화 가능 하위 클래스를 구현하는 것은 불가능할 수도 있다.** 

**상위 클래스에 무인자 생성자가 없다면 직렬화 가능 하위 클래스 구현은 불가능하다.** 따라서 상속을 고려한 직렬화 불가능 클래스에서는 무인자 생성자를 제공하는 것이 어떨지 반드시 따져봐야 한다. 클라이언트가 제공하는 데이터가 있어야 불변식을 충족할 수 있다며느 무인자 인자는 사용할 수 없다. 
이런 문제를 피하면서 직렬화 불가능 상속 클래스에 무인자 생성자를 추가할 방법이 있다.

```java
public abstract class AbstractFoo {
	private int x, y; 
	private enum State { NEW, INITIALIZING, INITIALIZED };
	private final AtomicReference<State> init = new AtomicReference<State>(State.NEW);

	public AbstractFoo(int x, int y) { initialize(x, y); }
	protected AbstractFoo() { }

	protected final void initialize(int x, int y) {
		if (!init.compareAndSet(State.NEW, State.INITIALIZING))
			throw new IllegalStateException("Already initialized");
		this.x = x;
		this.y = y;
		// Do anything else the original constructor did
		init.set(State.INITIALIZED);
	}

	protected final int getX() {
		checkInit();
		return x;
	}

	protected final int getY() {
		checkInit();
		return y;
	}

	private void checkInit() {
		if (init.get() != State.INITIALIZED)
			throw new IllegalStateException("Uninitialized");
	}
}
```

```java
public class Foo extends AbstractFoo implements Serializable {
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		int x = s.readInt();
		int y = s.readInt();
		initialize(x, y);
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		s.writeInt(getX());
		s.writeInt(getY());
	}

	// Constructor does not use the fancy mechanism
	public Foo(int x, int y) { super(x, y); }

	private static final long serialVersionUID = 1856835860954L;
}
```

- 객체 상태를 저장하는 변수는 `final` 이 될 수 없다. `initialize` 가 설정해야 하기 때문이다.
- 모든 `public`, `protected` 메소드는 작업 전에 반드시 `checkInit` 메소드를 호출해야 한다.

<br/>

## 내부 클래스는 Serializable 을 구현하면 안된다.

내부 클래스에는 바깥 객체(*enclosing instance*) 에 대한 참조를 보관하고, 바깥 유효범위(*enclosing scope*) 의 지역 변수 값을 보관하기 위해 
컴파일러가 자동으로 생성하는 **인위생성 필드(synthetic field)** 가 있다. 익명 클래스나 지역 클래스 이름과 마찬가지로, 언어 명세서에는 이런 필드가 어떻게 클래스 정의에 들어가는지 나와있지 않다.

- 따라서 **내부 클래스의 기본 직렬화 형식은 정의될 수 없다.**
- 그러나 **정적 멤버 클래스는 Serializable 을 구현해도 된다.**

<br/>

## 요약

- Serializable 구현은 심각히 고민해 봐야 한다. 
- 계승을 설계하는 클래스라면 더욱 조심해야 한다. 
- 상속은 허용하지만 직렬화를 구현하고 싶지 않은 경우 하위 클래스가 Serializable 을 허용하려면 무인자 생성자를 제공해야 한다.
