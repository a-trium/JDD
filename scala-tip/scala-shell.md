# Scala-shell Tips for JAVA developer

## Intro

scala 는 기본적으로 jvm 위에서 돌아간다. 깊이는 아니더라도, 약간의 문법과 lambda 기반의 collection function들을 배워둔다면, 짤막한 테스트나, 업무에 필요한 데이터를 추출하는데 사용할 수 있다.

앞으로 들 예는 지극히 작성자의 업무에서 유용하게 사용한 것이다.
(덧붙여 k 는 작성자가 제일 좋아하는 임시 변수이름일뿐 아무 의미가 없음)

## 준비
brew install scala

shell 에서 'scala' 입력하여 실행
혹은, intelliJ Scala Console을 이용해도 되지만, 셋팅이 오래걸리므로 패스
(일부 기본으로 추가되지 않는 자바 클래스를 import 구문으로 추가하기에는 intelliJ가 좋다) 

## Google Drive 공유권한 이메일 리스트 추출
공유창에서 전체 리스트를 복사
scala shell 에 :pa 를 입력하고, 뉴라인을 포함하여 붙여넣는다.
```scala
val k = """[붙여넣은 데이터-SKP Sentinel님(나)
sentinelskp@gmail.com
소유자	
이병준
neinasdfuj@gmail.com
수정 가능 	
Justin Lee
imjasdfuni@gmail.com]"""  
```
(앞에 val k = 을 사용하지 않아도 자동으로 변수 이름이 붙여짐 res#)

shell 에서 다단계로, 진행상태 확인 가능
k.split("\n")

k.split("\n").filter(p=>p.contains("@"))

k.split("\n").filter(p=>p.contains("@")).mkString(", ")

마지막에 추출된 이메일 리스트를 새로운 문서에 추가하는데 사용함. 예제는 3개뿐이지만, 업무상 20+인 경우가 대부분이므로 아주 유용하게 사용중

### Scala 변수선언의 종류, val과 var
***val*** 은 re-assign이 불가능한 변수(immutable)를 선언하는데 사용되며, var이 꼭 필요한 극히 드문 경우를 제외하고 scala에서 애용되는 방식이다. 위의 예제에서 split, filter, mkString을 할때마다, 새로운 val 객체가 생성됨

***var*** 은 자바와 마찬가지로 re-assgin이 가능하며, 작성자는 Map 데이터 정의에 사용해본 경험이 있다.

### Scala Collecion function w/ lambda
예제에 filter(p=>p.contains("@")) 에서 parameter로 사용된 lambda는 single statement 를 포함하고 있다. 
(multiline 인 경우 아래와 같이 구현됨. scala 에서는 별도의 return 구문없이 가장 마지막에 위치한 것이 return 된다. 또한 입력되지 않은 변수 type에 대해 문맥을 판단해 자동으로 mapping 하여 준다. 아래의 p:String은 이를 명시해준 것)
```scala
k.split("\n").filter{p:String=> 
//do something else
p.contains("@")
} 
```
collection function 의 경우 collection의 element를 하나씩 parameter lambda로 대입하여 실행시킨다. 여기서는 해당 string element에 "@"이 포함된 것인지를 나타내는 boolean 값이 return 되는데, filter 함수는 이 값이 true인 경우만 collection에 남겨둔다.
다른 대표적인 함수로 map이 있는데 위의 예제와 동일한 lambda를 사용하여 shell에 입력해보자.
```scala
k.split("\n").map(p=>p.contains("@"))
```
shell에 나온 결과를 보면 알다시피,
Array[String(type in original collection)]에서 Array[Boolean(Any u defined in lambda)]가 된다.
(Any 는 Java 의 Object와 비슷한 아이다.)
