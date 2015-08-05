# 규칙41. 오버로딩할 때는 주의하라

```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
             return "Set";
    }
    public static String classify(List<?> list) {
             return "List";
    }
    public static String classify(Collection<?> c) {
             return "Unknown Collection";
    }

    public static void main(String[] args) {
             Collection<?>[] collections = {
                        new HashSet<String>(),
                        new ArrayList<BigInteger>(),
                        new HashMap<String, String>().values()
             };
             for(Collection<?> c: collections) {
                        System.out.println(classify(c));
             }
             System.out.println(classify(new ArrayList<>()));
    }
}
```

Set, List, Unknown Collection을 순서대로 출력할것 같지만, Unknown Collection만 세번 호출한다.
오버로딩 된 메서드는 컴파일 시점에 호출이 결정되기 때문에, 컴파일 시점에 자료형이 `Collection<?>`으로 동일하기 때문이다.
때문에 아래와 같이 해야한다.

```java
public static String classify(Collection<?> c) {
         return c instanceof Set? "Set" : c instanceof List? "List" : "Unknown Collection";
}
```

오버로딩을 사용할 때는 혼란스럽지 않게 사용 할 수 있도록 주의해야 한다.
`오버로딩이 혼란스러운 상황`에는 논쟁의 여지가 있지만, 가장 안전하고 보수적인 전략은 같은수의 인자를 갖는 두개의 오버로딩 메서드를 API에 포함시키 않는 것이다. 오버로딩대신 이름을 다른것으로 바꿀 수 있기 때문이다.
