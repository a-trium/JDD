#4.4 함수 호출과 this
4.4.1 arguments객체

함수 호출 시 함수 형식에 맞지 않게 인자를 넘겨도 에러발생 안함
아래의 예 : 정의된 함수의 인자보다 적거나 많은 인자로 함수를 호출

function func(arg1, arg2){
	console.log(arg1, arg2);
}
func();//undefined undefined
func(1);//1 undefined
func(1,2);//1 2
func(1,2,3);//1 2

넘겨지지않은 인자 : undefined
초과된 인자 :  무시됨

이러한 특성 때문에 런타임시 호출된 인자의 개수를 확인하고 이에 따라 동작을 다르게 해줘야 함 -> arguments객체!
함수 호출시 인수들과 함께 arguments객체 및 this객체가 함수 내부로 전달 됨
Arguments객체 : 함수 호출할때 넘긴 인자들이 배열 형태로 저장된 객체
Arguments객체는 실제 배열이 아닌 유사배열 객체임!
즉 length프로퍼티가 있으므로 배열과 유사하게 동작하지만 배열이 아니므로 배열메서드 사용시 에러 발생
유사배열객체에서 배열메서드를 사용하는 방법 -> "call"과 "apply"메서드를 이용한 명시적인 this 바인딩

function add(a,b){
	console.dir(arguments);
	return a+b;
}
console.log(add(1));//NaN
console.log(add(1,2));//3
console.log(add(1,2,3));//3

Arguments객체의 3가지 요소
1. 호출시 넘겨진 인자(배열형태) : 함수 호출시 첫번째 인자는 0번 인덱스
2. length 프로퍼티 : 호출 시 넘겨진 인자의 개수
3. callee 프로퍼티 : 현재 실행중인 함수의 참조값

Arguments객체는 매개변수 개수가 정확하게 정해지지 않은 함수를 구현하거나, 전달된 인자에 따라 다른 처리 해줄 때 유용

function sum(){
	var result = 0;

	for(var i=0; i<arguments.length; i++){
		result+=arguments[i];
	}
	return result;
}
console.log(sum(1,2,3));//6
console.log(sum(1,2,3,4,5,6,7,8,9));//45

4.4.2 함수호출 패턴과 this바인딩
- This의 이해가 어려운 이유 : 함수가 호출되는 방식(호출 패턴)에 따라 this가 다른 객체를 참조(this 바인딩)

4.4.2.1 객체의 메서드 호출할 때 this바인딩
- 객체의 프로퍼티가 함수일 경우 이를 메서드라고 함
- 메서드를 호출할 때, 메서드 내부 코드에서 사용한 this는 해당 메서드를 호출한 객체로 바인딩 됨

var myObject = {
	name : 'foo',
	sayName : function(){
		console.log(this.name);
	}
};
var otherObject = {
	name : 'bar'
}
otherObject.sayName = myObject.sayName;
myObject.sayName();//foo
otherObject.sayName();//bar

myObject객체에서 sayName()호출 시, this는 myObject객체 가리키고,
otherObject객체에서 sayName()호출 시, this는 otherObject객체 가리킴
-> sayName()메서드에서 사용된 this는 자신을 호출한 객체에 바인딩
-> this는 자신을 호출한 객체에 바인딩됨!

4.4.2.2 함수를 호출할 때 this바인딩
- 자바스크립트에서 함수 호출 시, 해당 함수 내부 코드에서 사용된 this는 전역 객체에 바인딩.
- 브라우저에서 자바스크립트를 실행하는 경우 전역객체는 window객체
- Node.js와 같은 자바스크립트 언어를 통해 서버 프로그래밍을 할 수 있게끔 해주는 자바스크립트 런타임 환경에서의 전역객체는 global객체가 됨
자바스크립트의 모든 전역변수는 이러한 전역 객체의 프로퍼티들

var foo = "I'm foo";
console.log(foo);//I'm foo
console.log(window.foo);//I'm foo

var test = "This if test";
console.log(window.test);//This is test

var sayFoo = function(){
	console.log(this.test);//This is test
};
sayFoo();

자바스크립트의 모든 전역변수는 전역객체의 프로퍼티들
전역변수는 전역객체(window)의 프로퍼티로도 접근할 수 있음
this는 전역 객체에 바인딩됨.
sayFoo()함수가 호출된 시점에서, this는 전역객체인 window에 바인딩 됨!
this.test는 window.test

//전역변수 value
var value = 100;
//myObject객체 생성
var myObject = {
	value: 1,
	func1: function(){
		this.value += 1;
		console.log('func1() called. this.value : ' + this.value);

		//func2()내부함수
		func2 = function(){
			this.value +=1;
			console.log('func2() called. this.value : ' + this.value);

			func3 = function(){
				this.value += 1;
				console.log('func3() called. this.value : ' + this.value);
			}
			func3();//func3()내부 함수 호출
		}
		func2();//func2()내부 함수 호출
	}
};
myObject.func1();

//func1()메서드의 호출 -> func2()내부 함수 호출 -> func3()내부 함수 호출
메서드 내의 this는 자신을 호출한 객체를 가리킴.
func1()에서 사용된 this :  myObject를 가리킴
//기대결과
func1() called - this.value : 2;
func2() called - this.value : 3;//func1을 부모함수로 하여, func2의 내부함수의 this는 부모함수의 this와 같이 myObject를 가리킴
func3() called - this.value : 4;//func2을 부모함수로 하여, func3의 내부함수의 this는 부모함수의 this와 같이 myObject를 가리킴


//출력결과
func1() called - this.value : 2;
func2() called - this.value : 101;//window.value에 1더한 값
func3() called - this.value : 102;//window.value에 1더한 값

자바스크립트에서는 내부함수 호출 패턴을 정의해놓지 않음
내부함수도 함수이므로 호출 시 함수 호출로 취급됨
따라서 함수 호출 패턴 규칙에 따라, 내부함수의 this는 전역객체(window)에 바인딩됨
+ 해결방안 : 부모함수의 this를 내부함수가 접근가능한 다른 변수에 저장하는 것
+ 보통, this값을 저장하는 변수의 이름을 that이라고 지음

//전역변수 value
var value = 100;
//myObject객체 생성
var myObject = {
	value: 1,
	func1: function(){
		var that = this;
		this.value += 1;
		console.log('func1() called. this.value : ' + this.value);

		//func2()내부함수
		func2 = function(){
			that.value +=1;
			console.log('func2() called. this.value : ' + that.value);

			func3 = function(){
				that.value += 1;
				console.log('func3() called. this.value : ' + that.value);
			}
			func3();//func3()내부 함수 호출
		}
		func2();//func2()내부 함수 호출
	}
};
myObject.func1();

//출력결과
func1() called - this.value : 2;
func2() called - this.value : 3;//부모함수인 func1()의 변수 that에 접근, that변수로 func1()의 this가 바인딩 된 객체인 myObject에 접근 가능
func3() called - this.value : 4;//부모함수인 func2()의 변수 that에 접근, that변수로 func1()의 this가 바인딩 된 객체인 myObject에 접근 가능
//기존 부모함수의 this를 that이라는 변수에 저장하고, 이후에 내부 변수에서는 that으로 부모함수의 this가 가리키는 객체에 접근
자바스크립트에서는 이러한 this바인딩의 한계 극복 위해 this바인딩을 명시적으로 할 수 있는 "call"과 "apply"메서드를 제공
jQuery, underscore.js등과 같은 자바스크립트 라이브러리들의 경우 "bind"라는 이름의 메서드를 통해 사용자가 원하는 객체를 this에 바인딩할 수 있는 기능 제공

4.4.2.3 생성자 함수를 호출할 때 this바인딩
자바스크립트에서 객체 생성하는 방법 2가지 : "객체 리터럴 방식", "생성자 함수 이용하는 방식"
생성자 함수는 말그대로 자바스크립트의 객체를 생성하는 역할
기존 함수에 new 연산자를 붙여서 호출하면 해당 함수는 생성자 함수로 동작
반대로 생각하면, 일반 함수에 "new"를 붙여 호출하면 원치 않은 생성자 함수처럼 동작할 수 있음
따라서 자바스크립트 스타일 가이드에서는 특정 함수가 생성자 함수로 정의되어 있음을 알리려고 "함수 이름의 첫 문자를 대문자로 쓰기"를 권하고 있음
자바스크립트에서 생성자 함수를 호출할 때, 생성자 함수 코드 내부에서 this는 메서드와 함수 호출방식에서의 this바인딩과 다르게 동작!

+ 생성자 함수가 동작하는 방식 : new연산자로 자바스크립트 함수를 생성자로 호출 시 다음과 같은 순서로 동작
1. 빈 객체 생성 및 this 바인딩
생성자 함수가 실행되기 전 빈 객체가 생성됨
바로 이 객체가 생성자 함수가 생성하는 객체이며, this 로 바인딩됨.
이 후 생성자 함수 코드 내부에서 사용된 this 는 이 빈 객체를 가리킴
그런데 여기서 생성된 객체는 엄밀히 빈 객체는 아님
자바스크립트의 모든 객체는 자신의 부모인 프로토타입 객체와 연결되어 있음
이를 통해 부모 객체의 프로퍼티나 메서드를 자신의 것처럼 사용
-> 생성자 함수가 생성한 객체는 자신을 생성한 생성자 함수의 prototype프로퍼티가 가리키는 객체를 자신의 프로토타입 객체로 설정

2. this를 통한 프로퍼티 생성
함수 코드 내부에서 this 를 이용해서, 앞에서 생성한 빈 객체에 동적으로 프로퍼티나 메서드를 생성할 수 있음

3. 생성된 객체 리턴
리턴문이 없을 경우 : this 로 바인딩 된 새로 생성한 객체가 리턴됨, 명시적으로 this 를 리턴해도 결과는 같음
(생성자 함수가 아닌 일반 함수를 호출할때, 리턴값이 명시되어 있지 않으면 undefined 가 리턴됨)
리턴값이 새로 생성한 객체가 아닌 다른 객체를 리턴하는 경우 : 생성자 함수를 호출했다고 하더라도, this 가 아닌 해당 객체가 리턴됨

예제 4-28 생성자 함수의 동작방식 / Person이라는 생성자 함수를 통해 foo라는 객체 만드는 예제
//Person() 생성자 함수
var Person = function (name) {
	//함수 코드 실행 전
	this.name = name;
	//함수 리턴
}
//foo 객체 생성
var foo = new Person('foo');//Person함수를 생성자로 호출
console.log(foo.name);//(출력값)foo

1. Person함수가 생성자로 호출되면, 함수 코드가 실행되기 전 빈 객체 생성됨
2. 생성된 빈 객체는 Person()생성자 함수의 prototype프로퍼티가 가리키는 객체(Person.prototype객체)를 [[Prototype]]링크로 연결해서 자신의 프로토타입으로 설정
3. 이렇게 생성된 객체는 생성자 함수 코드에서 사용되는 this 로 바인딩 됨
4. this 가 가리키는 빈 객체에 name 이라는 동적 프로퍼티를 생성
5. 리턴값이 특별히 없으므로, this로 바인딩한 객체가 생성자 함수의 리턴값으로 반환돼서 foo 변수에 저장됨

객체 리터럴방식과 생성자 함수를 통한 객체 생성 방식의 차이
객체 리터럴 방식 : 같은 형태의 객체를 재생성할 수 없다, 프로토타입 객체가 Object(실제로는 Object.prototype)
생성자 함수 방식 : 호출을 할 때 다른 인자를 넘김으로써 같은 형태의 서로 다른 객체 생성할 수 있다, 프로토타입 객체가 Person(실제로는 Person.prototype)
+ 차이가 발생하는 이유 : 자바스크립트에서 객체는 자신을 생성한 생성자 함수의 prototype프로퍼티가 가리키는 객체를 자신의 프로토타입 객체로 설정
					객체리터럴 방식에서는 객체 생성자함수는 Object(), 생성자 함수 방식의 경우는 생성자 함수 자체.
//객체 리터럴 방식으로 foo객체 생성
var foo = {
	name: 'foo',
	age: 35,
	gender: 'man'
};
console.dir(foo);

//생성자 함수
function Person(name, age, gender){
	this.name = name;
	this.age = age;
	this.gender = gender;
}

//Person생성자 함수를 이용해 bar객체, baz객체 생성
var bar = new Person('bar', 25, 'woman');
console.dir(bar);

var baz = new Person('baz', 17, 'woman');
console.dir(baz);

+생성자 함수를 new를 붙이지 않고 호출할 경우:
new 를 붙여서 함수를 호출하면, 생성자 함수로 동작함
객체 생성을 목적으로 작성한 생성자 함수를 new 없이 호출하거나, 일반함수에 new 를 붙여서 호출할 경우 오류발생
이유 : 일반 함수 호출과 생성자 함수 호출할 때 this 바인딩이 다르기 때문
일반함수 호출의 경우 : this 가 window 전역 객체에 바인딩
생성자 함수 호출의 경우 : this 는 새로 생성되는 빈 객체에 바인딩

function Person(name, age, gender){
	this.name = name;
	this.age = age;
	this.gender = gender;
}

var qux = Person('qux', 20, 'man');
console.log(qux);//undefined

console.log(window.name);//qux
console.log(window.age);//20
console.log(window.gender);//man

Person()을 new 없이 일반 함수 형태로 호출 할 경우, this 는 함수 호출이므로 전역객체인 window 객체로 바인딩 됨
이 코드는 Person객체를 생성해서 이를 qux변수에 저장하려는 원래 의도와 달리, this 가 바인딩 된 window객체에 동적으로 name, age, gender프로퍼티가 생성된 것
Person()함수에 별도의 리턴값이 없는 이유 : 생성자 함수는 별도의 리턴값이 정해져 있지 않은 경우 새로 생성된 객체가 리턴되지만, 일반함수는 undefined 가 리턴됨


