#Chapter07 함수형 프로그래밍
##7.1 함수형 프로그래밍의 개념

- 함수의 조합으로 작업을 수행함을 의미
- 중요한 건, 이 작업이 이루어지는 동안 데이터와 상태는 변하지 않는다는 점
- 변할 수 있는 것은 오로지 함수!
- 함수가 바로 연산의 대상이 됨
- 특정 문자열을 암호화하는 함수가 여러개 있다

```javascript
//f1,f2,f3은 입력값이 정해져 있지 않고, 서로 다른 암호화 알고리즘만 있음
f1 = encrypt1;
f2 = encrypt2;
f3 = encrypt3;
//pure_value는 암호화할 문자열, encrypted_value는 암호화된 문자열
//get_encrypted()는 암호화 함수를 받아서 pure_value를 암호화한 후 반환
encrypted_value = get_encrypted(f1);
encrypted_value = get_encrypted(f2);
encrypted_value = get_encrypted(f3);
```

- pure_value는 작업에 필요한 데이터고 변화하지 않는다
- get_encrypted()가 작업하는 동안 변할 수 있는 것은 오로지 들어오는 함수 뿐
- f1,f2,f3은 외부에 아무런 영향을 미치지 않는 함수. 즉, '순수함수'다
- get_encrypted()함수는 인자로 f1,f2,f3함수를 받는다
- 이는 함수를 또 하나의 값으로 간주하여 함수의 인자 혹은 반환값으로 사용할 수 있는 함수. 즉, '고계함수'다
- 이 예에서 프로그래머는 입력으로 넣을 암호화 함수를 새롭게 만들어 암호화 방식을 개선할 수 있다
- 이와 같이 내부 데이터 및 상태는 그대로 둔 채 제어할 함수를 변경 및 조합함으로써 원하는 결과를 얻어내는 것이 함수형 프로그래밍의 중요한 특징!
- 높은 수준의 모듈화가 가능하다
- `순수함수`의 조건을 충족하는 함수 구현으로 모듈집약적인 프로그래밍이 가능하다
- 함수형프로그램이의 반대 개념이 `명령형프로그래밍`
- `명령형 프로그래밍`은 컴퓨터가 수행할 일의 명령을 순서대로 기술하는 프로그래밍 방식
- 함수형 프로그래밍 언어처럼 입력값을 받고 출력값을 계산하는 순수한 의미의 함수도 있지만, 특정작업을 수행하는 여러 명령이 기술되어 있는 함수도 있다
- 이러한 종류의 함수를 프로시저(procedure)라고 한다. 예)printf

```c
int ret = printf("print this to screen\n");
```

- printf함수 역시 입력값과 결과값이 있지만 중요한 것은 결과값이 아니라 printf함수가 실행되면서 입력값을 화면에 출력하는 동작이 중요
- 결과값은 이 동작이 제대로 수행되었는지 알려주는 보조적인 역할
- 이처럼, 특정 작업의 순차적인 명령을 기술하는데 중점을 둔다
- 이는 함수형프로그래밍의 순수함수와 거리가 멀다
- 함수형 프로그래밍 함수는 순수 함수로서 외부에 아무런 영향을 주지 않는 선에서 자신의 로직을 처리하여 결과를 반환하는 역할 수행
- 결과값을 얻는 것이 함수를 호출한 목적이고, 결과값으로 또다른 작업을 처리함

##7.2자바스크립트에서의 함수형 프로그래밍
- 자바스크립트에서도 함수형 프로그래밍이가능하다. 다음을 지원하기 때문!
	- 일급객체로서의 함수
	- 클로저

```javascript
var f1 = function(input) {
	var result;
	/*암호화 작업 수행*/
	result = 1;
	return result;
}
var f2 = function(input) {
	var result;
	/*암호화 작업 수행*/
	result = 2;
	return result;
}
var f3 = function(input) {
	var result;
	/*암호화 작업 수행*/
	result = 3;
	return result;
}
var get_encrypted = function(func) {
	var str = 'zzoon';

	return function() {
		return func.call(null, str);
	}
}
var encrypted_value = get_encrypted(f1)();
console.log(encrypted_value);
var encrypted_value = get_encrypted(f2)();
console.log(encrypted_value);
var encrypted_value = get_encrypted(f3)();
console.log(encrypted_value);
```

- 이것이 가능한 이유는 함수가 일급객체로 취급되기 때문
- 함수의 인자로 함수를 넘기고, 함수를 받환받을 수도 있다
- 변수 str이 영향을 받지 안헥 하려고 클로저를 사용
- get_encrypted()함수에서 반환하는 익명함수가 클로저
- 클로저에서 접근하는 변수 str은 외부에서 접근할 수 없음

##7.2.1배열의 각 원소 총합 구하기
- 배열의 각 원소 총 합 구하기

```javascript
function sum(arr) {
	var len = arr.length;
	var i = 0, sum = 0;

	for(; i<len; i++) {
		sum += arr[i];
	}
	return sum;
}
var arr = [1,2,3,4];
console.log(sum(arr));
```

- 배열의 각 원소 총 곱 구하기

```javascript
function multiply(arr) {
	var len = arr.length;
	var i = 0, result = 1;

	for(; i<len; i++) {
		result *= arr[i];
	}
	return result;
}
var arr = [1,2,3,4];
console.log(multiply(arr));
```

- 위의 두 코드는 명령형 프로그래밍 방식
- 또 다른 방식의 산술을 원할 경우 새로운 함수를 다시 구현해야 함
- 함수형 프로그래밍을 이용하면 이러한 수고를 덜 수 있음

```javascript
function reduce(func, arr, memo) {
	var len = arr.length;
	i = 0;
	accum = memo;

	for (; i<len; i++) {
		accum = func(accum, arr[i]);
	}
	return accum;
}
var arr = [ 1,2,3,4 ];

var sum = function(x,y) {
	return x+y;
};
var multiply = function(x,y) {
	return x*y;
};
console.log(reduce(sum, arr, 0));
console.log(reduce(multiply, arr, 1));
```
- 이와 같이 코드를 간결하게 작성하는 것이 가능
- 또 다른 연산 나오더라도 해당 연산을 하는 함수 작성해서 reduce()함수로 결과 얻을 수 있다
- 높은 모듈화를 이룰 수 있다

##7.2.2팩토리얼
- 명령형프로그래밍 방식의 팩토리얼

```javascript
function fact(num) {
	var val = 1;
	for (var i = 2; i<=num; i++){
		val = val *i;
	}
	return val;
}
console.log(fact(10));
```

- 아니면 다음과 같이 재귀함수 호출

```javascript
function fact(num) {
	if (num == 0) return 1;
	else return num* fact(num-1);
}
console.log(fact(10));
```

- 팩토리얼의 특성을 생각해보자
- 10!을 구한 후 20!을 실행한다고 가정하자
- 20!을 실행할 때는 앞에서 실행한 10!을 중복하여 계산함
- 이렇게 중복되는 값, 즉 앞에서 연산한 결과를 캐시에 저장하여 사용할 수 있는 함수를 작성하면 성능향상에 도움이됨

```javascript
var fact = function() {
	var cache = {'0':1};
	var func = function(n) {
		var result = 0;

		if(typeof(cache[n]) === "number") {
			result = cache[n];
		} else {
			result = cache[n] = n * func(n-1);
		}
		return result;
	}
	return func;
}();
console.log(fact(10));
console.log(fact(20));
```
- fact는 cache에 접근 가능한 클로저를 반환받는다
- cache는 팩토리얼을 연산한 값을 저장하고 있다
- 연산을 수행하는 과정에서 cache에 저장된 값이 있으면 곧바로 그 값을 반환
- 한 번 연산된 값을 저장하고 있으므로, 중복연산을 피하여 더 나은 성능의 함수 구현할 수 있다

##7.2.3 피보나치 수열
- 메모이제이션 기법을 적용

```javascript
var fibo = function() {
	var cache = {'0' : 0, '1' : 1};
	var func = function(n) {
	if (typeof(cache[n]) ==  'number') {
		result = cache[n];
	} else {
		result = cache[n] = func(n-1) + func(n-2);
	}
	return result;
	}
	return func;
}();
console.log(fibo(10));//55
```
- 클로저를 이용하여 cache를 캐시로 사용하는 점이 팩토리얼방식과 같다
- 팩토리얼과 피보나치 수열을 계산하는 함수를 인자로 받는 함수를 생각해보자
- 아래의 cacher함수는 사용자 정의함수와 초기 cache값을 받아 연산 수행
- 사용자는 이 함수의 인자로 피보나치 수열을 연산하는 함수 혹은 팩토리얼을 연산하는 함수를 아래와 같이 정의할 수 있다.

```javascript
var cacher = function(cache, func) {
	var calculate = function(n) {
	if (typeof(cache[n]) ==='number') {
		result = cache[n];
	} else {
		result = cache[n] = func(calculate, n);
	}
	return result;
	}
	return calculate;
};

var fact = cacher({'0':1}, function(func, n) {
	return n*fact(n-1);
});
var fibo = cacher({'0':0, '1':1}, function(func, n) {
	return func(n-1) + func(n-2);
});
console.log(fact(10));//3628800
console.log(fibo(10));//55
```

##7.3 자바스크립트에서의 함수형 프로그래밍을 활용한 주요 함수
##7.3.1 함수 적용
- Function.prototype.apply함수로 함수 호출을 수행할 수 있다는 것을 배웠다
- 함수 적용은 함수형 프로그래밍에서 사용되는 용어
- 함수형 프로그래밍에서는 특정 데이터를 여러 함수를 적용시키는 방식으로 작업을 수행
- 함수는 단순히 입력을 넣고 출력을 받는 기능 뿐만 아니라, 인자 혹은 반환 값으로 전달된 함수를 특정 데이터에 적용시키는 개념으로 이해해야 한다
- 그래서 자바스크립트에서도 함수를 호출하는 역할을 하는 메서드를 apply로 지은 것
- func.apply(Obj, Args)와 같은 함수 호출을 'func함수를 Obj객체와 Args인자 배열에 적용시킨다'라고 표현할 수 있다

##7.3.2 커링
- 특정함수에서 정의된 인자의 일부를 넣어 고정시키고, 나머지를 인자로 받는 새로운 함수를 만드는 것

```javascript
function calculate(a,b,c) {
	return a*b+c;
}
function curry(func) {
	var args = Array.prototype.slice.call(arguments, 1);

	return function() {
		return func.apply(null, args.concat(Array.prototype.slice.call(arguments)));
	}
}
var new_func1 = curry(calculate, 1);
console.log(new_func1(2,3));//5
var new_func2 = curry(calculate, 1, 3);
console.log(new_func2(3));//6
```

- calculate함수는 인자 세 개를 받아 연산 수행하고 결과값을 반환
- curry()함수로 첫 번째 인자를 1로 고정시킨 새로운 함수 new_func1(), 두번째 인자를 1과 3으로 고정시킨 new_func2()함수를 새로 만들 수 있다
- curry()함수의 역할이 중요. curry()함수로 넘어온 인자를 args에 담아 놓고, 새로운 함수 호출로 넘어온 인자와 합쳐서 함수를 적용
- 커링은 함수형프로그래밍 언어에서 기본적으로 지원하는데, 자바스크립트에서는 기본으로 제공하지 않음
- 다음과 같이 Function.prototype에 커링함수를 정의하여 사용할 수 있음

```javascript
Function.prototype.curry = function() {
	var fn = this, args = Array.prototype.slice.call(arguments);
	return function() {
		return fn.apply(this, args.concat( Array.prototype.slice.call(arguments)));
	};
};
```

- calculate()함수의 첫번째 인자와 세번째 인자를 고정하고 싶다면?

```javascript
function calculate(a,b,c) {
	return a*b+c;
}
function curry2(func) {
	var args = Array.prototype.slice.call(arguments, 1);
	return function(){
		var arg_idx = 0;
		for (var i=0; i < args.length && arg_idx < arguments.length; i++){
			if(args[i] === undefined)
				args[i] = arguments[arg_idx++];
		}
		return func.apply(null, args);
	}
}
var new_func = curry2(calculate, 1, undefined, 4);
console.log(new_func(3));//7
```

- curry2()함수를 사용할 때 주의할 점은 calculate()함수가 원하는 인자를 전부 넣어야 한다는 것
- 고정시키지 않을 인자를 undefined로 넘기면 된다
- curry2()에서는 undefined인 요소를 새로운 함수를 호출할 때 넘어온 인자로 대체
- 이처럼 함수를 부분적으로 적용하여 새로운 함수를 반환받는 방식을 `함수의 부분적용`이라고 한다
- 기존 함수로 인자가 비슷한 새로운 함수를 정의하여 사용하고 싶을 때, 이와 같이 `커링`을 사용한다

##7.3.3 bind
```javascript
Function.prototype.bind = function (thisArg) {
	var fn = this,
	slice = Array.prototype.slice,
	args = slice.call(arguments, 1);
	return function() {
		return fn.apply(thisArg, args.concat(slice.call(arguments)));
	};
}
```
- bind()함수는 커링 기법을 활용한 함수
- 커링과 같이 사용자가 고정시키고자 하는 인자를 bind()함수를 호출할 때 인자로 넘겨주고 반환받은 함수를 호출하면서 나머지 가변인자를 넣어줄 수 있다
- curry()함수와 다른 점은 함수 호출 시 this에 바인딩 시킬 객체를 사용자가 넣어줄 수 있다는 점
- curry()함수가 자바스크립트 엔진에 내장되지 않은 것도 bind()함수로 충분히 커버 가능하기 때문

```javascript
var print_all = function(arg) {
	for (var i in this) console.log(i + " : " + this[i]);
	for (var i in arguments) console.log(i + " : " + arguments[i]);
}
var myobj = { name: "zzoon" };

var myfunc = print_all.bind(myobj);
myfunc();

var myfunc1 = print_all.bind(myobj, "iamhjoo", "others");
myfunc1("insidejs");
//name : zzoon
//0 : iamhjoo
//1 : others
//2 : insidejs
```
- myfunc()함수는 myobj객체를 this에 바인딩시켜서 print_all()함수르 ㄹ실행하는 새로운 함수
- myfunc1()을 실행하면 인자도 bind()함수에 모두 넘겨짐
- 이와같이 특정함수에 원하는 객체를 바인딩하여 새로운 함수를 사용할 때 bind()함수가 사용된다

##7.3.4 래퍼
- 래퍼란 특정 함수를 자신의 함수로 덮어쓰는 것
- 사용자는 원래 함수의 기능을 잃어버리지 않은 상태로 자신의 로직을 수행할 수 있어야 한다
- `오버라이드`개념과 유사

```javascript
function wrap(object, method, wrapper) {
	var fn = object[method];
	return object[method] = function() {
		return wrapper.apply(this, [fn].concat(
		//return wrapper.apply(this, [fn.bind(this)].concat(
		Array.prototype.slice.call(arguments)));
	};
}
Function.prototype.original = function(value) {
	this.value = value;
	console.log("value : " + this.value);
}
var mywrap = wrap(Function.prototype, "original", function(orig_func, value) {
	this.value = 20;
	orig_func(value);
	console.log("wrapper value: " + this.value);
});
var obj = new mywrap("zzoon");
//value : zzoon
//wrapper value: 20
```
- Function.prototype에 original이라는 함수: 인자로 들어온 값을 value에 할당하고 출력
- 이를 덮어쓰기 위해 wrap함수를 호출
- 세번째인자로 넘긴 익명함수를 Function.prototype.original에 덮어쓰려는 것
- 익명 함수의 첫 번째 인자로 원래 함수의 참조를 받을 수 있다
- 이 참조로 원래 함수를 실행하고 자신의 로직을 수행할 수 있다

##7.3.5 반복 함수
##7.3.5.1 each
- each()함수는 배열의 각 요소 혹은 객체의 각 프로퍼티를 하나 씩 꺼내서 차례대로 특정 함수에 인자로 넣어 실행시키는 역할 수행
- 대부분의 자바스크립트 라이브러리에 기본적으로 구현되어 있는 함수
- 보통 each혹은 foreach라는 이름으로 제공됨

```javascript
function each(obj, fn, args){
	if ( obj.length == undefined ){
		for( var i in obj ){
			fn.apply( obj[i], args || [i, obj[i]]);
		}
	}else{
		for( var i = 0; i < obj.length; i++)
			fn.apply( obj[i], args || [i, obj[i]]);
	}
	return obj;
};

each([1,2,3], function(idx, num) {
	console.log(idx + ": " +num);
});

var zzoon = {
	name : "zzoon",
	age : 30,
	sex : "Male"
};

each(zzoon, function(idx, value) {
	console.log(idx + ": " +value);
});
//0: 1
//1: 2
//2: 3
//name: zzoon
//age: 30
//sex: Male
```
- obj에 length가 있는경우는 배열, 없는 경우는 객체이다
- 루프를 돌면서 각 요소를 인자로 하여 차례대로 함수를 호출
- each()하수는 다양한 언어에서 기본적으로 제공하나 each()함수에서 사용자 함수를 호출할 때 넘기는 인자의 순서나 구성이 라이브러리에 따라 다를 수 있음

##7.3.5.2 map
- map()함수는 배열의 각 요소를 꺼내서 사용자 정의 함수를 적용시켜 새로운 값을 얻은 후 배열에 넣는다

```javascript
Array.prototype.map = function(callback) {
	//this가 null인지 배열인지 체크
	//callback이 함수인지 체크
	var obj = this;
	var value, mapped_value = 0;
	var A = new Array(obj.length);

	for( var i = 0; i < obj.length; i++){
		value = obj[i];
		mapped_value = callback.call(null, value);
		A[i] = mapped_value;
	}
	return A;
};
var arr = [1,2,3];
var new_arr = arr.map(function(value){
	return value*value;
});
console.log(new_arr);//[1, 4, 9]
```
- 배열 각 요소의 제곱값을 새로운 요소로 하는 배열을 반환받는 예제

###7.3.5.3 reduce
- reduce()는 배열의 각 요소를 꺼내서 사용자의 함수를 적용시킨 뒤, 그 값을 계속해서 누적시키는 함수

```javascript
Array.prototype.reduce = function(callback, memo) {
	//this가 null인지, 배열인지 체크
	//callback이 함수인지 체크

	var obj = this;
	var value, accumulated_value = 0;

	for(var i = 0; i<obj.length; i++){
		value = obj[i];
		accumulated_value = callback.call(null, accumulated_value, value);
	}
	return accumulated_value;
};
var arr = [1,2,3];
var accumulated_val = arr.reduce(function(a,b) {
	return a + b*b;
});
console.log(accumulated_val);
```
- 배열의 각 요소를 순차적으로 제곱한 값을 더해 누적된 값을 반환받는 예제이다
