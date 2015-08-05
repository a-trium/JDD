# Rule 62 - 메서드에서 던져지는 모든 예외에 대해 문서를 남겨라

## 컴파일타임 예외

- **컴파일타임 예외는 독립적으로 선언하고, 해당 예외가 발생하는 상황은 Javadoc** `@throws` 태그를 사용해서 정확하게 밝혀라.
- `throws Exception` 이나 `throws Throwable` 은 절대로 피해야 한다.

## 런타임 예외

- **런타임 예외에 대해 문서화를 잘 남기면, 선행조건 (precondition) 이 무엇인지 효과적으로 알려줄 수 있다.**

> Javadoc `@throws` 태그를 사용해서 메서드에서 발생할 수 있는 모든 무점검 예외에 대한 문서를 남겨라. 하지만 메서드의 
선언부에 `throws` 로 모든 무점검 예외를 나열하면, API 사용자가 어떤것이 점검 예외인지 알 수 없다.
 
## 클래스를 고쳐도 소스 호환성과 이진 호환성은 보장된다.

```java
class Beta {
    // throws ClassAlphaException1 ClassAlphaException2
    public void useAlphaClass() {
        Alpha.method1();
    }
}
```

추후에 `Alpha.method1()` 에서 `ClassAlphaException3` 를 던질수도 있다.

## Summary

요약하자면, 메서드가 던질 가능성이 있는 모든 예외를 문서로 남기자. `abstract` 메서드에도 문서를 만들어야 한다. 
점검지정예외는 `throws` 절에 적되, 무점검 예외는 적지 말아야 한다.




