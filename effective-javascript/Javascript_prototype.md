
#4.5프로토타입 체이닝
4.5.1 프로토타입의 두 가지 의미
자바스크립트는 프로토타입 기반의 객체지향 프로그래밍을 지원
자바와 같은 객체지향 프로그래밍에서는 클래스를 정의하고 이를 통해 객체를 생성하지만, 자바스크립트에서는 이러한 클래스 개념이 없음
대신, 객체 리터럴이나 생성자 함수로 객체 생성
생성된 객체의 부모 객체가 바로 '프로토타입'객체
상속과 마찬가지로, 자식 객체는 부모객체가 가진 프로퍼티 접근이나 메서드를 상속받아 호출하는 것이 가능
자바스크립트의 모든 객체는 자신의 부모인 프로토타입 객체를 가리키는 참조링크 형태의 숨겨진 프로퍼티가 있음
ECMAScript에서는 이를 암묵적 프로토타입 링크 라고 부름
이 링크는 모든 객체의 [[Prototype]]프로퍼티에 저장됨
이러한 링크는 [[Prototype]]링크라고 명명
함수객체의 prototype프로퍼티와, 객체의 숨은 프로퍼티인 [[Prototype]]링크를 구분해야 함
자바스크립트의 모든 객체는 자신을 생성한 생성자 함수의 prototype프로퍼티가 가리키는 프로토타입객체를 자신의 부모객체로 설정하는 [[prototype]]링크로 연결
```javascript
//Person생성자함수
function Person(name){
	this.name = name;
}
//foo객체생성
var foo = new Person('foo');
console.dir(Person); //function Person(name) -> Prototype: Person, __proto__ : function()
console.dir(foo); //Person -> __proto__ : Person
```
- Person()생성자 함수는 prototype프로퍼티로 프로토타입객체(Person.prototype)를 가리킴
- Person()생성자 함수로 생성된 foo객체는 Person()함수의 프로토타입 객체를 [[Prototype]]링크로 연결
- Person()생성자 함수의 prototype프로퍼티와 foo객체의 __proto__프로퍼티가 같음
- prototype프로퍼티 : 함수의 입장에서 자신과 링크된 프로토타입 객체를 가리킴
- [[Prototype]]링크 : 객체입장에서 자신의 부모 객체인 프로토타입 객체를 내부의 숨겨진 링크로 가리킴
- 결국, 자바스크립트에서 객체를 생성하는 것은 생성자 함수의 역할
- 생성된 객체의 실제 부모역할은 생성자의 prototype프로퍼티가 가리키는 프로토타입 객체
- __proto__프로퍼티는 모든 객체에 존재하는 숨겨진 프로퍼티, 객체 자신의 프로토타입 객체를 가리키는 참조 링크

4.5.2 객체 리터럴방식으로 생성된 객체의 프로토타입 체이닝
프로토타입체이닝 : 객체는 자기자신의 프로퍼티 뿐만 아니라, 자신의 부모역할을 하는 프로토타입 객체의 프로퍼티 또한 마치 자신의 것처럼 접근하는 게 가능하도록 함
```javascript
var myObject = {
	name: 'foo',
	sayName: function(){
		console.log('My Name is' + this.name);
	}
};
myObject.sayName(); //My Name is foo
console.log(myObject.hasOwnProperty('name')); //true
console.log(myObject.hasOwnProperty('nickName')); //false
myObject.sayNickName(); //Uncaught TypeError
//hasOwnProperty()메서드 : 이 메서드를 호출한 객체에 인자로 넘긴 문자열 이름의 프로퍼티나 메서드가 있는지 체크하는 자바스크립트 표준 API함수
//객체 리터럴로 생성한 객체는 Object()라는 내장생성자 함수로 생성한 것
//Object()생성자 함수도 함수객체이므로 property라는 프로퍼티 속성이 있음
//myObjecr는 Object()함수의 prototype프로퍼티가 가리키는 Object.prototype객체를 자신의 프로토타입 객체로 연결
```
++ 프로토타입체이닝 : 해당 객체에 접근하려는 프로퍼티 또는 메서드가 없다면 [[Prototype]]링크를 따라 자신의 부모 역할을 하는 프로토타입 객체의 프로퍼티를 차례대로 검색하는 것

- myObject 는 hasOwnProperty()메서드가 없다
- myObject객체의 [[Property]]링크를 따라, 부모역할을 하는 Object.prototype프로토타입 객체 내에 hasOwnProperty()메서드가 있는지 검색

4.5.3 생성자 함수로 생성된 객체의 프로토타입 체이닝
자바스크립트에서 모든 객체는 자신을 생성한 생성자 함수의 prototype프로퍼티가 가리키는 객체를 자신의 프로토타입객체(부모객체)로 취급
```javascript
//Person()생성자함수
function Person(name, age, hobby){
	this.name = name;
	this.age = age;
	this.hobby = hobby;
}
//foo객체
var foo = new Person('foo', 30, 'tennis');

//프로토타입 체이닝
console.log(foo.hasOwnProperty('name')); //true

//Person.prototype객체 출력
console.dir(Person.prototype);//Person

//foo객체의 생성자 함수는 Person()함수
//foo객체의 프로토타입 객체는 자신을 생성한 Person생성자 함수 객체의 prototype프로퍼티가 가리키는 객체
//즉, foo객체의 프로토타입객체는 Person.prototype
//foo.hasOwnProperty()메소드 호출 -> foo객체에 이 메소드 없음 -> 부모객체인 Person.prototype객체에서 찾음 -> 함수에 연결된 프로토타입 객체는 디폴트로 constructor프로퍼티만을 가진 객체이므로 이 메소드 없음 -> Person.prototype 역시 자바스크립트 객체이므로 Object.prototype을 프로토타입객체로 가지므로 Object.prototype객체의 hasOwnProperty() 메소드를 실행
```
4.5.4 프로토타입 체이닝의 종점
Object.prototype객체는 프로토타입 체이닝의 종점
객체리터럴방식이나 생성자 함수 방식에 상관없이 모든 자바스크립트 객체는 프로토타입 체이닝으로 Object.prototype객체가 가진 프로퍼티와 메서드에 접근가능하고 서로 공유가 가능

4.5.5 기본 데이터 타입 확장
자바스크립트의 숫자, 문자열, 배열 등에서 사용되는 표준 메서드들의 경우 이들의 프로토타입인 Number.prototype, String.prototype, Array.prototype등에 정의되어 있음
기본 내장 프로토타입 객체 또한 Object.prototype을 자신의 프로토타입으로 가지고 있어서 프로토타입체이닝으로 연결됨
자바스크립트는 Object.prototype, String.prototype등과 같이 표준 빌트인 프로토타입 객체에도 사용자가 직접 정의한 메서드들을 추가하는 것을 허용
```javascript
String.prototype.testMethod = function(){
	console.log('This is the String.prototype.testMethod()');
};
var str = "This is test";
str.testMethod(); //This is the String.prototype.testMethod()
console.dir(String.prototype); //String.prototype에 testMethod()를 추가한 것이 보임
```
4.5.6 프로토타입도 자바스크립트 객체다
- 함수 생성시 자신의 prototype프로퍼티에 연결되는 프로토타입 객체는 디폴트로 constructor프로퍼티만을 가진 객체
- 포토타입 객체 역시 자바스크립트 객체
- 일반 객체처럼 동적으로 프로퍼티를 추가하고 삭제하는 것이 가능
- 변경된 프로퍼티는 실시간으로 프로토타입 체이닝에 반영됨
```javascript
//Person생성자함수
function Person(name){
	this.name = name;
}
//foo객체
var foo = new Person('foo');
//foo.sayHello(); //Error발생
//프로토타입객체에 sayHello()메서드정의
Person.prototype.sayHello = function(){ //foo객체의 프로토타입객체인 Person.prototype객체에 동적으로 sayHello()메서드 추가
	console.log('Hello');
}
foo.sayHello(); //Hello // foo객체는 sayHello()메서드가 없지만, 프로토타입체이닝으로 Person.prototype객체에서 sayHello()메서드 검색
```
4.5.7 프로토타입 메서드와 this 바인딩
프로토타입객체는 메서드를 가질 수 있다.
프로토타입 메서드 내부에서 this를 사용하면, 메서드호출패턴에 의해서 this는 그 메서드를 호출한 객체에 바인딩된다.
```javascript
//Person()생성자함수
function Person(name){
	this.name = name;
}
//getName()프로토타입 메서드
Person.prototype.getName = function(){
	return this.name;
};
//foo객체 생성
var foo = new Person('foo');
console.log(foo.getName()); //foo
//getName()메서드를 foo객체에서 찾을 수 없으므로 프로토타입 체이닝 발생
//foo의 프로토타입 객체인 Person.prototype에서 getName()메서드발견 !
//getName()메서드를 호출한 객체는 foo이므로 this는 foo객체에 바인딩됨
//Person.prototype객체에 name프로퍼티 동적 추가
Person.prototype.name = 'Person';
console.log(Person.prototype.getName()); //Person
//프로토타입 체이닝이 아니라, Person.prototype객체에 접근해서 바로 getName()메서드를 호출
//getName()메서드를 호출한 객체는 Person.prototype이므로 this는 여기에 바인딩됨
//Person.prototype객체에 name프로퍼티를 동적으로 추가하고 'Person'저장했으므로 this.name은 'Person'
```
4.5.8 디폴트 프로토타입은 다른 객체로 변경이 가능
디폴트 프로토타입 객체는 합수가 생성될 때 같이 생성되며, 함수의 prototype프로퍼티에 연결됨
자바스크립트는 함수를 생성할 때, 해당 함수와 연결되는 디폴트 프로토타입 객체를 다른 일반객체로 변경하는 것이 가능
생성자 함수의 프로토타입 객체가 변경되면, 변경된 시점 이후에 생성된 객체들은 변경된 프로토타입객체[[Prototype]]링크를 연결함
```javascript
//Person()생성자함수
function Person(name){
	this.name = name;
}
console.log(Person.prototype.constructor); //Person(name)
//foo객체 생성
var foo = new Person('foo');
console.log(foo.country); //undefined
//디폴트 프로토타입 객체 변경
Person.prototype = {
	country:'Korea'
};
console.log(Person.prototype.constructor); //Object()
//bar객체 생성
var bar = new Person('bar');
console.log(foo.country); //undefined
console.log(bar.country); //korea
console.log(foo.constructor); //Person()
console.log(bar.constructor); //Object()

```
4.5.9 객체의 프로퍼티 읽기나 메서드를 실행할 때만 프로토타입 체이닝이 동작
- 객체의 특정 프로퍼티를 읽으려고 할때, 프로퍼티가 해당 객체에 없는 경우 프로토타입 체이닝이 발생
- 반대로 객체에 있는 특정 프로토타입 값을 쓰려고 하면, 프로토타입 체이닝이 일어나지 않음
- 객체에 없는 프로퍼티에 값을 쓰려고 할 경우, 동적으로 객체에 프로퍼티를 추가하기 때문


