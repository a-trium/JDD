# Rule 46 - For 문 보다는 For-each 문을 사용하라


```java
// 자바 1.5 이전 컬렉션 순회를 위해 사용했던 방법

for (Iterator i = c.iterator(); i.hasNext() {
    doSomething();
    i.next();
}

// 자바 1.5 이전 배열 순회를 위해 사용했던 방법

for (int i = 0; i < arr.length; i++) {
    doSomething();
}

// 자바 1.5 이후 배열과 컬렉션 순회를 위해 사용할 수 있는 방법

for (Element e : elems) {
    doSomething();
}
```

`for` 문을 사용하면, 중첩 순환문 작성시 다음과 같은 오류를 만들 수 있다.

```java
enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

Collection<Suit> suits = Arrays.asList(Suit.values());
Collection<Rank> ranks = Arrays.asList(Rank.values());
List<Card> deck = new ArrayList<Card>();

for (Iterator<Suit> i = suits.iterator(); i.hasNext(); )
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(i.next(), j.next()));
```

또 다른 버그로는 

```java
// Same bug, different symptom!
enum Face { ONE, TWO, THREE, FOUR, FIVE, SIX }
Collection<Face> faces = Arrays.asList(Face.values());

for (Iterator<Face> i = faces.iterator(); i.hasNext(); )
    for (Iterator<Face> j = faces.iterator(); j.hasNext(); )
        System.out.println(i.next() + " " + j.next());
```

이 문제를 해결하려면, 다음과 같이 바깥 순환문 유효범위 안에 변수를 선언해야 한다.

```java
for (Iterator<Suit> i = suits.iterator(); i.hasNext(); ) {
    Suit suit = i.next();
    
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(suit, j.next()));
}
```

<br/>

`for-each` 문으로는 **컬렉션**, **배열**, **iterable 인터페이스 구현체** 를 모두 순회할 수 있다. 그러나 아래의 세 경우에 대해서는 적용할 수 없다.

- 삭제를 위해서는 반복자의 `remove` 를 호출해야 한다.
- 변환을 위해서는 반복자나 배열 첨자가 필요하다.
- 병렬 순회가 필요할 경우

