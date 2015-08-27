5.3.1 전역실행컨텍스트의 스코프 체인
```javascript
var var1 = 1;
var var2 = 2;
console.log(var1);
console.log(var2);
```
- 함수가 선언되지 않아 함수 호출이 없음
- 전역실행 컨텍스트가 생성되고 변수객체(활성객체)가 만들어짐
- 전역 실행 컨텍스트 단 하나만 실행되고 있어서 참조할 상위 컨텍스트가 없음
- 자신이 최상위에 위치하는 변수객체인 것
- 변수객체의 [[scope]]는 변수객체 자신을 가리킴
- 이변수객체가 곧 전역객체가 됨

[[scope]] ---> 0 전역객체
var1
var2
this

5.3.2 함수를 호출한 경우 생성되는 실행 컨텍스트의 스코프 체인
```javascript
var var1 = 1;
var var2 = 2;
function func(){
	var var1 = 10;
	var var2 = 20;
	console.log(var1);
	console.log(var2);
}
func();
console.log(var1);
console.log(var2);
```
- 위 예제 실행시 전역실행컨텍스트가 생성되고 func()함수객체가 만들어짐
- func()함수객체가 생성될 때 func()함수객체의 [[scope]]는 현재실행되는 컨텍스트의 변수객체에 있는 [[scope]]를 그대로 가짐
- func()함수객체의 [[scope]]는 전역변수객체가 됨
- func()함수 실행시 새로운 컨텍스트 만들어짐
- 이 func()컨텍스트의 스코프 체인은 실행된 함수의 [[scope]]프로퍼티를 그대로 복사한 후, 현재 생성된 변수객체를 복사한 스코프 체인의 맨 앞에 추가
- func 실행 컨텍스트의 스코프 체인은 [func변수객체 - 전역객체]가 됨
- 스코프 체인 = 현재 실행 컨텍스트의 변수객체 + 상위컨텍스트의 스코프 체인
- 스코프 체인으로 식별자 인식이 이루어짐
- 식별자 인식은 스코프 체인의 첫번재 변수 객체부터 시작
- 식별자와 대응되는 이름을가진 프로퍼티가 있는지 확인
- 스코프 체인의 가장앞에 있는 객체가 변수객체이므로 이 객체에 있는 공식인자, 내부함수, 지역변수에 대응되는지 먼저 확인
- this는 식별자가 아닌 키워드로 분류되므로, 스코프 체인의 참조 없이 접근 가능
- 스코프체인을 사용자가 임의로 수정하는 키워드 : with
- 이제 함수 호이스팅의 원인을 알 수 있다!
```javascript
var foo;
function bar(){
	console.log("bar and x = " + x);
}
var x;
//foo();//TypeError
bar();
foo = function(){
	console.log("foo and x = " + x);
};
x = 1;
```
- 위 코드의 함수 생성과정에서 변수foo, 함수객체 bar, 변수x를 차례로 생성
- foo와 x에 undefined가 할당됨
- foo()에서 TypeError발생하는 이유 : foo가 선언되어있지만, 함수가 아니기 때문

5.4클로저
5.4.1 클로저의 개념
```javascript
function outerFunc(){
	var x = 10;
	var innerFunc = function(){ console.log(x); }
	return innerFunc;
}
var inner = outerFunc();
inner();
```
- 함수는 일급객체. 함수를 다른함수의 인자로 넘길 수 있고 return으로 함수를 통째로 받을 수 있기에 위 코드가 가능.
- innerFunc의 실행컨텍스트 : innerFunc변수객체 - outerFunc변수객체 - 전역객체
- outerFunc실행컨텍스트가 끝났지만 outerFunc변수객체는 여전히 남아있고 innerFunc의 스코프 체인으로 참조되고 있음
- 때문에 가비지 컬렉션의 대상이 되지 않고, 접근 가능하게 살아있음
- 이게 바로 `클로저`의 개념!
- 클로저 : 이미 생명주기가 끝난 외부 함수의 변수를 참조하는 함수. closure의 의미는 함수가 자유변수에 대해 닫혀있다는 의미.
- outerFunc에서 선언한 x를 참조하는 innerFunc가 클로저!
- 자유변수 : 클로저로 참조되는 외부변수, 여기서는 outerFunc의 x와 같은 변수
- 지역 변수에 접근하려면, 함수가 종료되어 외부함수의 컨텍스트가 반환되더라도 변수 객체는 반환되는 내부함수의 스코프 체인에 그대로 남아있어야 접근 가능
- 클로저는 자바스크립트에만 있는 개념이 아니라 여러언어에서 차용되는 개념임
- 함수를 일급객체로 취급하는언어(함수형언어)에서 주로 사용
- 클로저를 사용한 코드가 그렇지 않은 코드보다 메모리 부담이 높음
- 하지만 클로저를 쓰지 않는 것은 자바스크립트의 강력한 기능 하나를 무시하는 것

5.4.2클로저의 활용
5.4.2.1 특정함수에 사용자가 정의한 객체의 메서드 연결하기
```javascript
function HelloFunc(func){
	this.greeting = "hello";
}
HelloFunc.prototype.call = function(func){
	func ? func(this.greeting) : this.func(this.greeting);
}
var userFunc = function(greeting){
	console.log(greeting);
}
var objHello = new HelloFunc();
objHello.func = userFunc;
objHello.call();
```
//결과 : hello
- func프로퍼티로 참조되는 함수를 call()함수로 호출
- func프로퍼티에 자신이 정의한 함수를 참조시켜 호출
- userFunc()함수를 정의하여 HelloFunc.func()에 참조시킨 뒤 HelloFunc()의 지역변수인 greeting을 화면에 출력
```javascript
function saySomething(obj, methodName, name){
	return (function(greeting){
		return obj[methodName](greeting, name);
	});
}
function newObj(obj, name){
	obj.func = saySomething(this, "who", name);
	return obj;
}
newObj.prototype.who = function(greeting, name){
	console.log(greeting + "" + (name || "everyone"));
}
var obj1 = new newObj(objHello, "rang");
obj1.call();
```
//결과 : hello rang
- 정해진 형식의 함수를 콜백해주는 라이브러리가 있을 경우, 사용자 정의 함수를 호출할 때 유용하게 사용됨
- 예 )브라우저의 onclick, onmouserover과 같은 이벤트 핸들러의 형식은 function(event) {}인데, event 외에 원하는 인자를 더 추가한이벤트 헨들러를 사용하고 싶을 경우 클로저를 활용!

5.4.2.2 함수의 캡슐화
"I am XXX. I live in XXX. I'am XX years old"라는 문장 출력시, XX부분은 사용자에게 인자로 입력받아 값을 출력하는 경우
```javascript
var buffAr = [
	'I am ',
	'',
	'. I live in ',
	'',
	'.I\'am ',
	'',
	' years old.'
];

function getCompletedStr(name, city, age) {
	buffAr[1] = name;
	buffAr[3] = city;
	buffAr[5] = age;

	return buffAr.join('');
}

var str = getCompletedStr('jarang', 'seoul', 27);
console.log(str);// I am jarang. I live in seoul.I'am 27 years old.
```
단점 : buffAr이라는 배열은 전역변수로 외부에 노출되어있음
해결 : 클로저를 활용해 buffAr을 스코프에 넣고 사용하기
```javascript
var getCompletedStr = (function(name, city, age){
	var buffAr = [
		'I am ',
		'',
		'. I live in ',
		'',
		'.I\'am ',
		'',
		' years old.'
	];

	return (function(){
		buffAr[1] = name;
		buffAr[3] = city;
		buffAr[5] = age;

		return buffAr.join('');
	});
})();

var str = getCompletedStr('jarang', 'seoul', 27);
console.log(str);//결과값이 이상합니다 ㅠ_ㅠ..
```
- getCompletedStr에 익명의 함수를 즉시실행시켜 반환되는 함수를 할당
- 이 반환되는 함수가 클로저가 되고, 클로저는 자유변수 buffAr을 스코프 체인에서 참조

5.4.2.3 setTimeout()에 지정되는 함수의 사용자 정의
- setTimeout함수는 웹브라우저에서 제공하는 함수
- 첫번째로 넘겨지는 함수 실행의 스케쥴링을 할 수 있음
- 두번째 인자의 밀리초 단위 숫자 만큼의 시간 간격으로 해당 함수를 호출
```javascript
function callLater(obj, a, b){
	return (function(){
		obj["sum"] = a + b;
			console.log(obj["sum"]);
	});
}
var sumObj = {
	sum : 0
}
var func = callLater(sumObj, 1, 2);
setTimeout(func, 500);
```
- func에 callLater함수를 반환받아, setTimeout()함수의 첫번째 인자로 넣는다
5.4.3 클로저를 활용할 때 주의사항
5.4.3.1 클로저의 프로퍼티값이 쓰기 가능하므로 그 값이 여러번 호출로 항상 변할 수 있음
```javascript
function outerFunc(argNum){
	var num = argNum;
	return function(x){
		num += x;
		console.log("num: " + num);
	}
}
var exam = outerFunc(40);
exam(5);//45
exam(-10);//35
```
5.4.3.2 하나의 클로저가 여러 함수 객체의 스코프 체인에 들어가 있는 경우
```javascript
function func(){
	var x = 1;
	return {
		func1 : function(){ console.log(++x); },
		func2 : function(){ console.log(-x); }
	};
};
var exam = func();
exam.func1();//2
exam.func2();//-2
```

5.4.3.3 루프 안에서 클로저를 활용할 때 주의하자
```javascript
function countSeconds(howMany){
	for(var i = 1; i <= howMany; i++){
		setTimeout(function(){
		console.log(i);
		}, i*1000);
	}
};
countSeconds(3);//4가 세번찍힘
```
- setTimeout함수의 인자로 들어가는 함수는 자유변수 i를 참조
- 함수가 실행되는 시점은 countSeconds()함수의 실행이 종료된 이후
- i값은 이미 4가 된 상태
- setTimeout()로 실행되는 함수는 모두 4를 출력
```javascript
function countSeconds(howMany){
	for(var i = 1; i <= howMany; i++){
		(function(currentI){
			setTimeout(function(){
			console.log(currentI);
			}, currentI*1000);
		})(i);
	}
};
countSeconds(3);//1,2,3이 순차적으로 찍힘
```
- 실행함수를 실행시켜 루프 i값을 currentI에 복사해서 setTimeout()에 들어갈 함수에서 사용
