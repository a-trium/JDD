pply메서드를 이용한 명시적 this바인딩
call과 apply : this를 특정객체에 명시적으로 바인딩
모든 함수의 부모객체인 Function.prototype객체의 메서드
다음의 형식으로 apply()메서드 호출하는 것이 가능

function.apply(thisArg, argArray)

call()과 apply()메서드는 기능은 같고 넘겨받는 인자만 다름
apply()메서드를 호출하는 주체가 함수
apply()메서드도 this를 특정 객체에 바인딩할 뿐 본질적인 기능은 함수 호출
Person.apply() 이렇게 호출하면 이것의 기본기능은 Person()함수를 호출하는 것
thisArg :
- apply()메서드를 호출한 함수 내부에서 사용한 this에 바인딩할 객체
- 첫 번째로 넘긴 객체가 his로 명시적으로 바인딩 되는 것
argArray :
- 함수를 호출할 때 넘길 인자들의 배열 가리킴

apply()메서드 :
두번째 인자인 argArray배열을 자신을 호출한 함수의 인자로 사용하되, 이 내부에서 사용된 this는 첫번째 인자인 thisArg로 바인딩해서 함수를 호출

//생성자 함수
function Person(name, age, gender){
	this.name = name;
	this.age = age;
	this.gender = gender;
}
//foo빈객체 생성
var foo = {};

//apply()메서드 호출
Person.apply(foo, ['foo', 30, 'man']); // apply메서드를 이용해서 Person함수를 호출
console.dir(foo);
// 첫번째 인자로 넘긴 foo가 Person()함수에서 this로 바인딩
// apply메서드의 두번째인자로 넘긴 배열['foo', 30, 'man']은 Person()함수의 인자로 넘겨짐
// 결국 Person('foo', 30, 'man')함수를 호출하면서, this를 foo객체에 명시적으로 바인딩

call()메서드 :
apply()메서드와 기능 같지만, 두번째 인자에서 배열로 넘긴 것을 각각 하나의 인자로 넘김

Person.call(foo, 'foo', 30, 'man')

apply()메서드와 call()메서드의 장점 : this를 원하는 값으로 매핑해서 특정함수나 메서드를 호출할 수 있음
대표적 용도 : arguments객체와 같은 유사배열 객체에서 배열메서드를 사용하는 경우
arguments객체는 실제 배열이 아니므로 pop(), shift()와 같은 표준배열메서드사용불가
하지만 apply() 메서드를 이용하면 가능

function myFunction(){
	console.dir(arguments);
	//arguments.shift(); //에러발생 //배열에서는 shift()메서드 이용하여 첫 번째 원소 쉽게 삭제가능, arguments객체는 length만 가진 유사배열 객체이므로 에러발생

	//arguments객체를 배열로 변환
	var args = Array.prototype.slice.apply(arguments);
	//이러한 경우, apply()메서드로 arguments객체에서 마치 배열 메서드가 있는 것처럼 처리
	//Array.prototype.slice()메서드를 호출하되, this를 arguments객체로 바인딩하라.
	//arguments객체가 Array.prototype.slice()메서드를 자신의 메서드인 양, arguments.slice()와 같은 형태로 호출하라는 것
	//Array.prototype은 모든 배열 객체의 부모역할을 하는 자바스크립트 기본 프로토타입 객체로서, slice(), push(), pop()과 같은 배열 표준 메서드가 있음
	//slice(start,end)메서드는 배열의 start인덱스에서 end-1인덱스까지 복사한 배열을 리턴
	//이때, end인덱스 지정하지 않으면 기본값은 배열의 length값
	console.dir(args);
}
myFunction(1,2,3);
//결과
//console.dir(arguments)의 결과는 Arguments[3]
//arguments는 객체이므로 프로토타입이 Object.prototype
//console.dir(args)의 결과는 Array[3]
//args는 배열이므로 프로토타입이 Array.prototype

4.4.3함수 리턴
자바스크립트 함수는 항상 리턴값을 반환함(return문을 사용하지 않더라도)

4.4.3.1 규칙1) 일반함수나 메서드는 리턴값을 지정하지 않을 경우, undefined값이 리턴된다.

var noReturnFunc = function() {
	console.log('This function has no return statement');
};
var result = noReturnFunc();
console.log(result);

//결과
//This function has no return statement
//undefined

4.4.3.2 규칙2) 생성자 함수에서 리턴값을 지정하지 않을 경우 생성된 객체가 리턴된다.
생성자 함수에서 별도의 리턴값을 지정하지 않은 경우, this로 바인딩된 새로 생성된 객체가 리턴됨

예) 생성자함수에서 명시적으로 객체를 리턴했을 경우
// Person()생성자 함수
function Person(name, age, gender){
	this.name = name;
	this.age = age;
	this.gender = gender;

	//명시적으로 다른 객체 반환
	return {name:'bar', age:20, gender:'woman'};
}

var foo = new Person('foo', 30, 'man');
console.dir(foo); //리턴값에서 명시적으로 넘긴 객체나 배열이 리턴됨
//생성자 함수에서 넘긴 값이 객체가 아닌 불린, 숫자, 문자열의 경우 이러한 리턴값을 무시하고 this로 바인딩 된 객체가 리턴됨

function Person(name, age, gender){
	this.name = name;
	this.age = age;
	this.gender = gender;

	return 100;
}
var foo = new Person('foo', 30, 'man');
console.log(foo); //출력결과는 100이 아니라 foo객체값 //Person {name: "foo", age: 30, gender: "man"}
