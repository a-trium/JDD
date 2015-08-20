# Chapter 3

## @FuncitonalInterface 

> 함수형 인터페이스는 정확히 하나의 추상 메서드를 지정하는 인터페이스 람다는 전체 표현식을 함수형 인터페이스를 구현한 클래스의 인스턴스를 전달한다.

```java
@FunctionalInterface
interface Runnable {
    void run();
}
```

## Function Descriptor

함수형 인터페이스의 추상 메서드를 함수 디스크립터라 부른다.

## Primitive Type Support

언박싱을 피하기 위해 `IntPredicate` 와 같은 함수형 인터페이스를 제ㅗㅇ한다.

## Exception

```java
public class FileProcessor {
    @FunctionalInterface
    interface BufferedReaderProcessor {
        String process(BufferedReader br) throws IOException;
    }

    public static String processFile(String filePath,
                                     BufferedReaderProcessor processor) {
        String result = "default value";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            result = processor.process(br);
        } catch (IOException e){
            // handle e;
        }

        return "";
    }

    public static void apiUsage() {
        String filePath = "./data.txt";

        String oneLine = processFile(
                filePath,
                (BufferedReader br) -> br.readLine() + br.readLine());
    }
}
```

`BufferedReaderProcessor` 대신 `Function<T, R>` 을 사용할 경우 **checked exception** 을 사용할 수 없다. 만약 던지려면, 

- `BufferedReaderProcessor` 처럼 **checked exception** 을 던지는 함수형 인터페이스를 만들거나
- `RuntimeException` 으로 한번 감싸야 한다.

## Local Variable

람다 내부에서 사용되는 변수는, final 이거나 final 처럼 사용되어야 한다.

```java
@Test
public void test_local_var() {
    int a = 3;
    forEach(ns, n -> System.out.println(n + a));
    a = 5; // compile error!
}
```

람다에서 사용되는 외부 변수는 모두 **복사** 되기 때문에, 변수의 semantic 을 유지하려면 final 이어야 하기 때문이다.

## Method Reference

사용자는 메서드 레퍼런스만 지정하고, 컴파일러가 해당 메서드의 시그니쳐를 참조해서 컴파일하므로 가독성을 높일 수 있다.

```java
@Test
public void test_method_ref() {
    List<String> str = Arrays.asList("a", "b" ,"A", "B");
    str.sort(String::compareToIgnoreCase);
    assertEquals(Arrays.asList("a", "A", "b", "B"), str);
}
```

```java
(args) -> ClassName.staticMethod(args) = ClassName::staticMethod

(arg0, refs) -> arg0.instanceMethod(rest) = ClassName::instanceMethod

(args) -> expr.instanceMethod(args) = expr::InstanceMethod
```

## Constructor Reference

