# Rule 36 - @Override 어노테이션을 사용하자

```java
public boolean equals(Bigram b) { .. } // 오버라이딩 되지 않음! Object 인자를 받아야 오버라이딩 된다.
```

상위 자료형에 선언된 메서드를 재정의하는 모든 메서드에 `@Override` 어노테이션을 붙이도록 하면 공장히 많은 오류를 막을 수 있다. 
예외적으로 **non-abstract** 클래스에서 상위 클래스의 **abstract** 메서드를 재정의할 때는 붙이지 않아도 되지만, 붙여서 나쁠 것도 없다.

`
