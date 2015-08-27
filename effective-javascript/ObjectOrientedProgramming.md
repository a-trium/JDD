#Chapter06 객체지향프로그래밍
- 클래스기반의 언어와 프로토타입 기반의 언어 구분하기
- `클래스기반의 언어` :
	- `클래스`로 `객체`의 기본적인 형태와 기능 정의
	- `생성자`로 `인스턴스` 만들어서 사용
	- 클래스에 정의된 `메서드`로 여러 기능 수행
	- `모든 인스턴스`가 클래스에 정의된 대로 `같은 구조`이고 `런타임에 바꿀 수 없음`
	- `정확성, 안전성, 예측성` 등의 관점에서 프로토타입기반의 언어보다 나음
- `프로토타입기반의 언어` :
	- 객체의 자료구조, 메서드 등을 `동적으로 바꿀 수 있음`
	- 동적으로 자유롭게 객체의 구조와 동작 방식을 바꿀 수 있는 장점이 있음

##6.1 클래스, 생성자, 메서드
- 자바스크립트는 거의 모든 것이 `객체`이고 특히 `함수객체`로 많은 것을 구현
- 클래스, 생성자, 메서드도 모두 함수로 구현이 가능

```javascript
function Person(arg){
	this.name = arg;

	this.getName = function() {
		return this.name;
	}

	this.setName = function(value) {
		this.name = value;
	}
}
var me = new Person("zzoon");
console.log(me.getName());

me.setName("iamjarang");
console.log(me.getName());
```

- 함수 Person이 `클래스이자 생성자` 역할
- `new키워드`로 인스턴스 생성하여 사용
- me는 Person의 인스턴스로 name변수, getName(), setName()함수가 있음

```javascript
var me = new Person("me");
var you = new Person("you");
var him = new Person("him");
```

- 문제없어보이지만, 각 객체는 자기 영역에서 `공통적으로 사용할 수 있는 setName()과 getName()함수를 따로 생성`
- 즉, 불필요하게 `중복`되는 영역을 메모리에 올려놓고 사용함을 의미
- 해결 : `함수객체의 프로토타입`을 활용

```javascript
function Person(arg){
	this.name = arg;
}
Person.prototype.getName = function() {
	return this.name;
}

Person.prototype.setName = function(value) {
	this.name = value;
}
var me = new Person("me");
var you = new Person("you");
console.log(me.getName());
console.log(you.getName());
```

- Person 함수 객체의 `prototype프로퍼티에 getName()과 setName()함수를 정의`
- 각 객체는 각자 따로 함수객체를 생성할 필요 없이 setName()과 getName()함수를 프로토타입 체인으로 접근
- 더글락스 클락포드는 다음과 같이 메서드를 정의하는 방법을 소개

```javascript
Function.prototype.method = function(name, func){
	if(!this.prototype[name]){
		this.prototype[name] = func;
	}
}
```

- 이를 활용하면

```javascript
Function.prototype.method = function(name, func){
	this.prototype[name] = func;
}
function Person(arg){
	this.name = arg;
}
Person.method("setName", function(value){
	this.name = value;
});
Person.method("getName", function(){
	return this.name;
});
var me = new Person("me");
var you = new Person("you");
console.log(me.getName());
console.log(you.getName());
```

- 더글락스 클락포드는 `함수를 생성자를 사용하여 프로그래밍하는 것을 비추천`함
- 이유는 `생성된 함수는 new로 호출될 수 있을 뿐만 아니라`, `직접호출도 가능`하기 때문
- new로 호출될 때와 직접 호출될 때의 `this바인딩`이 다르다는 점이 문제
- 이러한 문제때문에 생성자로 사용되는 함수는 `첫글자를 대문자`를 쓸 것을 권고

##6.2 상속

- 객체 프로토타입 체인을 활용하여 상속을 구현
- 클래스기반 전통적인 상속 방식을 흉내내기 vs 클래스 개념 없이 객체의 프로토타입으로 상속을 구현하기(이를 `"프로토타입을 이용한 상속"` 이라고 함)

##6.2.1 `프로토타입을 이용한 상속`

- 더글락스 클락포드가 자바스크립트 객체를 상속하는 방법으로 오래전에 소개한 코드

```javascript
function create_object(o){
	function F() {}
	F.prototype = o;
	return new F();
}
```

- create_object()함수는 `인자로 들어온 객체를 부모로 하는 객체를 생성해서 반환`
- 빈 함수객체 F를 만들고, F.prototype프로퍼티에 인자로 들어온 객체를 참조
- `함수객체 F를 생성자로 하는 새로운 객체` 만들어서 반환
- `반환된 객체는 부모객체의 프로퍼티에 접근할 수 있고`, `자신만의 프로퍼티`를 만들 수도 있음
- 이렇게 프로토타입의 특성을 활용하여 상속을 구현하는 것이 프로토타입 기반의 상속임
- create_object()함수는 ECMAScript5에서 Object.create()함수로 제공되므로 따로구현할필요 없음

```javascript
var Person = {
	name : "jarang",
	getName : function() {
		return this.name;
	},
	setName : function(value) {
		this.name = value;
	}
}
function create_object(o){
	function F() {}
	F.prototype = o;
	return new F();
}
var student = create_object(Person);
student.setName("me");
console.log(student.getName());//me
```

- Person객체를 활용하여 student객체를 만들었음
- 클래스에 해당하는 생성자함수 만들지 않고, 그 클래스의 인스턴스를 따로 생성하지도 않음
- 단지 `부모객체에 해당하는 Person객체`와 이 객체를 프로토타입 체인으로 참조할 수 있는 `자식객체 student`를 만들어서 사용
- 자식은 `자신의 메서드를 재정의 혹은 추가로 기능을 더 확장`시킬 수 있어야함

```javascript
student.setAge = function(age){
	...
}
student.getAge = function(){
	...
}
```

- 이런방식의 확장은 지저분함
- 자바스크립트에서는 범용적으로 `extend()`라는 이름의 함수로 객체에 자신이 원하는 객체 혹은 함수를 추가시킴

```javascript
//jQuery 1.0의 extend함수
jQuery.extend = jQuery.fn.extend = function(obj,prop){
	if(!prop){prop = obj; obj = this;}
	//인자가 하나만 들어오면 현재객체(this)에 인자로 들어오는 객체의 프로퍼티 복사
	//두개 들어오면 첫번째 객체에 두번째객체 프로퍼티 복사
	for( var i in prop ) obj[i] = prop[i];
	//루프를 돌면서 prop프로퍼티를 obj로 복사
	return obj;
}
```

- jQuery.fn은 jQuery.prototype
- `jQuery함수 객체와 jQuery함수 객체의 인스턴스 모두 extend함수가 있다`는 말
- 즉, jQuery.extend()로 호출 가능하고, var elem = new jQuery(..);elem.extend();의 형태로도 호출가능
- 위의 `문제는 obj[i] = prop[i]이 부분이 얕은 복사를 의미`한다는 것
- 즉, 문자 혹은 숫자 리터럴 등이 아닌 객체(배열,함수 객체 포함)인 경우 해당 객체를 복사하지않고, 참조!
- 이는 두번째 객체의 프로퍼티가 변경되면 첫번째 객체의 프로퍼티도 변경됨을 의미
- `그러므로 보통 extend함수를 구현하는 경우 대상이 객체일 때 깊은 복사를 하는 것이 일반적`
- `jQuery1.7의 extend함수` : `얕은 복사를 할 것인지, 깊은 복사를 할 것인지를 선택`할 수 있게 구현되어 있음

```javascript
var Person = {
	name : "jarang",
	getName : function() {
		return this.name;
	},
	setName : function(value) {
		this.name = value;
	}
}
function create_object(o){
	function F() {}
	F.prototype = o;
	return new F();
}

function extend(obj,prop){
	if(!prop){prop = obj; obj = this;}
	for( var i in prop ) obj[i] = prop[i];
	return obj;
}

var student = create_object(Person);
var added = {
	setAge : function(age){
		this.age = age;
	},
	getAge : function(){
		return this.age;
	}
}
extend(student, added);
student.setAge(17);
console.log(student.getAge());//17
```

- `얕은복사를 사용하는 extend()함수를 사용해 student객체를 확장`시킴
- extend()함수는 사용자에게 유연하게 기능 확장을 할 수 있게 해주는 함수

##6.2.2 `클래스 기반의 상속`

- 앞절에서는 객체리터럴로 생성된 객체의 상속을 소개했지만, 여기서는 `클래스의 역할을 하는 함수로 상속을 구현`

```javascript
function Person(arg){
	this.name = arg;
}
Person.prototype.setName = function(value){
	this.name = value;
};
Person.prototype.getName = function(){
	return this.name;
};
function Student(arg){

}
var you = new Person("iamjarang");
Student.prototype = you;
var me = new Student("zzoon");
me.setName("zzoon");
console.log(me.getName());//zzoon
```

- `Student함수 객체의 프로토타입`이 `Person함수객체의 인스턴스(you)`를 참조
- S`tudent함수 객체로 생성된 객체 me의 [[prototype]]링크`가 `생성자의 프로토타입 프로퍼티  Student.prototype인 you`를 가리킴
- new Person()으로 만들어진 객체의 [[prototype]]링크는 Person.prototype을 가리키는 프로토타입 체인이 형성됨
- `객체 me`는 `Person.prototype프로퍼티에 접근가능`하고 `setName()과 getName()을 호출` 가능
- 이 코드의 문제 -> var me = new Student("zzoon"); 이 코드 에서 zzoon을 인자로 넘겼지만 반영하는 코드 없음
- 결국 생성된 `me객체는 빈 객체`

```javascript
function Person(arg){
	this.name = arg;
}
Person.prototype.setName = function(value){
	this.name = value;
};
Person.prototype.getName = function(){
	return this.name;
};
function Student(arg){

}
var you = new Person("iamjarang");
Student.prototype = you;
var me = new Student("zzoon");
<!-- me.setName("zzoon"); -->
console.log(me.getName());//iamjarang
```

- 이렇게 setName()안하고 getName()호출하면 iamjarang이 출력됨
- `setName()메서드가 호출되고 나서야 me객체에 name프로퍼티가 만들어짐`
- 부모의 생성자가 호출되지 않으면, 인스턴스의 초기화가 제대로 이루어지지않아서 문제 발생

```javascript
function Person(arg){
	this.name = arg;
}
Person.prototype.setName = function(value){
	this.name = value;
};
Person.prototype.getName = function(){
	return this.name;
};
function Student(arg){
	Person.apply(this, arguments);
}
var you = new Person("iamjarang");
Student.prototype = you;
var me = new Student("zzoon");
<!-- me.setName("zzoon"); -->
console.log(me.getName());//zzoon
```

- 이렇게 Student함수 안에서 새롭게 생성된 객체를 `apply함수의 첫번째 인자로 넘겨 Person함수를 실행`
- 이런 방식으로 `자식클래스의 인스턴스에 대해서도 부모 클래스의 생성자를 실행`시킬 수 있음
- 클래스 간 상속에서 `하위클래스의 인스턴스를 생성할 때, 부모 클래스의 생성자를 호출해하 하는 경우 필요한 방식`
- 현재 자식 클래스의 객체가 부모 클래스의 객체를 프로토타입 체인으로 직접 접근하지만, 부모클래스의 인스턴스와 자식 클래스의 인스턴스는 `독립적일 필요가 있음`

```javascript
function Person(arg){
	this.name = arg;
}
Function.prototype.method = function(name, func){
	this.prototype[name] = func;
}
Person.method("setName", function(value){
	this.name = value;
});
Person.method("getName", function(){
	return this.name;
});
function Student(arg){

}
function F() {};
F.prototype = Person.prototype;
Student.prototype = new F();
Student.prototype.constructor = Student;
Student.super = Person.prototype;

var me = new Student();
me.setName("zzoon");
console.log(me.getName());
```

- `빈 함수 객체`를 중간에 두어 Person의 인스턴스와 Student의 인스턴스를 서로 독립적으로 만들었음
- Person함수 객체에서 this에 바인딩되는 것은 Student의 인스턴스가 접근할 수 없다

```javascript
var inherit = function(Parent, Child){
	var F = function() {};
	return function(Parent, Child){
		F.prototype = Parent.prototype;
		Child.prototype = new F();
		Child.prototype.constructor = Child;
		Child.super = Parent.prototype;
	};
}();
```

- 위 코드에서 클로저(반환되는 함수)는 F()함수를 지속적으로 참조
- `F()는 가비지 컬렉션의 대상이 되지 않고 그대로 남아있음`
- F()의 생성은 단 한 번 이루어지고 inherit함수가 계속해서 호출되어도 함수 F()의 생성을 새로 할 필요 없음

##6.3캡슐화

- 캡슐화란 관련된 여러 정보를 하나의 틀 안에 담는 것
- 중요한 것은 정보의 공개여부(정보 은닉)
- 자바스크립트에서 정보은닉이라는 키워드 자체를 지원하지 않지만, 정보 은닉 가능함

```javascript
var Person = function(arg){
	var name = arg ? arg : "zzoon";

	this.getName = function(){
		return name;
	}

	this.setName = function(arg){
		name = arg;
	}
};
var me = new Person();
console.log(me.getName());//zzoon
me.setName("iamjarang");
console.log(me.getName());//iamjarang
console.log(me.name);//undefined
```

- `private멤버로 name선언`하고, public메서드로 setName(), getName()을 선언
- `this객체의 프로퍼티로 선언하면 외부에서 new키워드로 생성한 객체에 접근 가능`
- 하지만, `var로 선언된 멤버들은 외부에서 접근 불가능`
- public메서드가 클로저 역할을 하면서 private멤버인 name에 접근할 수 있음

```javascript
var Person = function(arg){
	var name = arg ? arg : "zzoon";

	return {
		getName : function(){
			return name;
		},
		setName : function(arg){
			name = arg;
		}
	};
}
var me = Person();
console.log(me.getName());
```

- Person함수를 호출하여 객체를 반환받는다
- 이 객체에 `Person함수의 private멤버에 접근가능한 메서드들이 담겨있음`
- 이 메서드들을 활용해서 private멤버에 접근
- 주의해야할 점 : `접근하는 private멤버가 객체나 배열이면` 얕은 복사로 참조만 반환하므로, 이후에 쉽게 변경할 수 있음

```javascript
var ArrCreate = function(arg){
	var arr = [1,2,3];

	return {
		getArr : function() {
			return arr;
		}
	};
}
var obj = ArrCreate();
var arr = obj.getArr();
arr.push(5);
console.log(obj.getArr());//[1,2,3,5]
```

- 이처럼 private멤버인 배열은 쉽게 변경이 됨
- 보통, 객체를 반환하지 않고, 객체의 주요정보를 새로운 객체에 담아서 반환하는 방법을 많이 사용
- `꼭 객체가 반환되어야 하는 경우에는 깊은 복사로 복사본을 만들어서 반환하자`

```javascript
var Person = function(arg) {
	var name = arg ? arg : "zzoon";

	var Func = function() {}
	Func.prototype = {
		getName : function() {
			return name;
		},
		setName : function(arg) {
			name = arg;
		}
	};

	return Func;
}();
var me = new Person();
console.log(me.getName());//zzoon
```

- 클로저를 활용해 name에 접근할 수 없게 함
- 즉시실행함수에서 반환되는 `Func이 클로저`가 되고, 이 함수가 참조하는 `name프로퍼티가 자유변수`가 됨
- 따라서 사용자는 name에 대한 접근이 불가능

