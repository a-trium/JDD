# 2. 생성자에 인자가 많을 경우는 Builder 패턴을 고려하라.

## 점층적 생성자 패턴

```java
// telescoping constructor anti-pattern

Pizza(int size) { ... }        
Pizza(int size, boolean cheese) { ... }    
Pizza(int size, boolean cheese, boolean pepperoni) { ... }    
Pizza(int size, boolean cheese, boolean pepperoni, boolean bacon) { ... }
```

점층적 생성자 패턴은 잘 동작하지만, **인자가 늘어나면 클라이언트 코드를 작성하기 어렵고, 무엇보다 읽기 어려운 코드가 된다**.

<br/>

## 자바빈 패턴

```java
public class Printer {

    @Inject @Informal Greeting greeting;
    
    private String name;
    private String salutation;

    public void createSalutation() {
        this.salutation = greeting.greet(name);
    }

    public String getSalutation() {
        return salutation;
    }

    public void setName(String name) {
       this.name = name;
    }

    public String getName() {
       return name;
    }
}
```

자바빈 패턴을 이용할 경우 함수 호출 1회로 필요한 클래스가 만들어지지 않기 때문에, 일관성이 일시적으로 깨질 수 있다.

<br/>

## 빌더 패턴

```java
public class User {
	private final String firstName; // required
	private final String lastName; // required
	private final int age; // optional
	private final String phone; // optional
	private final String address; // optional

	private User(UserBuilder builder) {
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.age = builder.age;
		this.phone = builder.phone;
		this.address = builder.address;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getAge() {
		return age;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public static class UserBuilder {
		private final String firstName;
		private final String lastName;
		private int age;
		private String phone;
		private String address;

		public UserBuilder(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public UserBuilder age(int age) {
			this.age = age;
			return this;
		}

		public UserBuilder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public UserBuilder address(String address) {
			this.address = address;
			return this;
		}

		public User build() {
			return new User(this);
		}

	}
}
```

```java
public User getUser() {
	return new
			User.UserBuilder('Jhon', 'Doe')
			.age(30)
			.phone('1234567')
			.address('Fake address 1234')
			.build();
}
```

[Rake-Android: RakeCore.java #L94](https://github.com/skpdi/rake-logger-android/blob/a7e22dfaed931eb0f3de98e8fd1e905f3669d9c8/rake/src/main/java/com/skp/di/rake/client/core/RakeCore.java#L90)
[Rake-Android: RakeCore.java #L53 Constructor](https://github.com/skpdi/rake-logger-android/blob/master/rake/src/main/java/com/skp/di/rake/client/core/RakeCore.java#L53)

1. 빌더 패턴을 이용하면 불변식(invariant) 적용할 수 있다.

불변식은, 메소드의 로직이 완료되었을 때 항상 만족해야 하는 조건이다. 빌더 패턴을 이용하면 `build` 메소드 내에서 이 식을 검사할 수 있다. 만족되지 않을 경우 `IllegalStateException` 을 던져야 한다.
 
[SO: What is class invariant in JAVA](http://stackoverflow.com/questions/8902331/what-is-a-class-invariant-in-java)
 
2. 자바의 제네릭을 이용하면 `Builder` 인터페이스를 만들 수 있다.
 
```java
public interface Builder<T> {
  T build();
}

Tree buildTree(Builder<? extends Node> nodeBuilder) {
	...
	...
}
```

3. `Class.newInstance` 는 항상 인자가 없는 생성자를 호출하는데, 그런 생성자가 없을 수도 있다. (런타임 예외 바랭)

클라이언트는 런타임에 `InstantiationException` 이나 `IllegalAccessException` 을 처리해야한다.

<br/>

빌더 패턴은 인자가 많지 않다면 오버헤드가 될 수 있다. 따라서 **인자가 많거나, 대부분의 인자가 선택적인 경우에 빌더 패턴을 활용하자.**

