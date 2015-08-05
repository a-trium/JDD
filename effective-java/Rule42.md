# 규칙42. varargs는 신중히 사용하라

```java
static int sum(int... args){
    int sum = 0;
    for(int arg : args)
        sum += arg;

    return sum;
}
```

때로는 0이 아니라 하나 이상의 값이 필요할때, 실행시점에 배열 길이를 검사해야함

```java
public static int min(int... args) {
    if(args.length == 0)
        throw new IllegalArgumentException("Too few arguments");

    int min = args[0];
    for(int i = 1; i < args.length; i++)
        if(args[i] < min)
            min = args[i];

    return min;
}
```

`args` 유효성 검사코드를 명시적으로 넣어야하며, 클라이언트에서 인자 없이 호출하는것이 불가능.

```java
public static int min(int firstArg, int... remainingArgs) {
    int min = firstArg;
    for(int arg: remainingArgs) {
        if(arg < min) min = arg;
    }
   return min;
}
```

위와같이 인자를 두개 받도록 선언하면 앞의 모든 문제를 해결할 수 있다.

> varargs는 정말 임의 갯수의 인자를 처리할 수 있는 메서드를 만들어야 할 때만 사용해야한다.
