# Rule 35 - 작명패턴 대신 어노테이션을 사용하자

## Built-in Java Annotations

- `@Deprecated`
- `@Override`
- `@SuppressWarnings`

## Retention Policy

```java
@Retention(RetentionPolicy.RUNTIME)
@Retention(RetentionPolicy.CLASS)
@Retention(RetentionPolicy.SOURCE) // not in .class file. used in build-time processing
```

## Target

```java
@Target(ElementType.ANNOTATION_TYPE)
@Target(ElementType.CONSTRUCTOR)
@Target(ElementType.FIELD)
@Target(ElementType.LOCAL_VARIABLE)
@Target(ElementType.METHOD)
@Target(ElementType.PACKAGE)
@Target(ElementType.PARAMETER)
@Target(ElementType.TYPE)
```

## Inherited, Documented

```java
@Inherited
public @interface MyAnnotation {}

@Documented
public @interface MyAnnotation {} // visible in the JavaDoc
```

## Test Runner Example

```java
// Annotations

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test { }

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Exception> value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MultipleExceptionTest {
    Class<? extends Exception>[] value();
}
```

```java
// Runner

public class TestRunner {
    static private int tests = 0;
    static private int passed = 0;
    public static void main(String[] args) throws ClassNotFoundException {

        String testSuiteClassName = "com.github.lambda.annotations.SampleTestSuite";
        runSuite(testSuiteClassName);
    }

    private static void runSuite(String suiteName) throws ClassNotFoundException {
        Class testClass = Class.forName(suiteName);

        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                runTest(m);
            } else if (m.isAnnotationPresent(ExceptionTest.class)) {
                runExceptionTest(m);
            } else if (m.isAnnotationPresent(MultipleExceptionTest.class)) {
                runMultipleExceptionTest(m);
            }

        }

        System.out.printf("Passed: %d, Failed %d\n", passed, tests - passed);
    }

    public static void runTest(Method m) {
        tests++;

        try {
            m.invoke(null);
            passed++;
        } catch (InvocationTargetException wrappedEx) {
            Throwable t = wrappedEx.getCause();

            // check if ExceptionTest
            Class<? extends Exception> exType = m.getAnnotation(ExceptionTest.class).value();

            if (exType.isInstance(t)) {
                passed++;
            } else {
                System.out.printf("ExceptionTest %s failed: expected %s, got %s\n",
                        m, exType.getName(), t);
            }

        } catch (Exception e) {
            // not static method
            System.out.println("INVALID @Test: " + m);
        }
    }

    public static void runExceptionTest(Method m) {
        tests++;

        try {
            m.invoke(null);
            System.out.printf("ExceptionTest %s failed: No exception\n", m);
        } catch (InvocationTargetException wrappedEx) {
            Throwable t = wrappedEx.getCause();

            Class<? extends Exception> exType = m.getAnnotation(ExceptionTest.class).value();

            if (exType.isInstance(t)) {
                passed++;
            } else {
                System.out.printf("ExceptionTest %s failed: expected %s, got %s\n",
                        m, exType.getName(), t);
            }

        } catch (Exception e) {
            // not static method
            System.out.println("INVALID @Test: " + m);
        }
    }

    public static void runMultipleExceptionTest(Method m) {
        tests++;

        try {
            m.invoke(null);
            System.out.printf("MultipleExceptionTest %s failed: No exception\n", m);
        } catch (InvocationTargetException wrappedEx) {
            Throwable t = wrappedEx.getCause();

            Class<? extends Exception>[] exTypes = m.getAnnotation(MultipleExceptionTest.class).value();
            int oldPassed = passed;

            for (Class<? extends Exception> exType : exTypes) {
                if (exType.isInstance(t)) {
                    passed++;
                    break;
                }
            }

            if (oldPassed == passed) {
                System.out.printf("MultipleExceptionTest %s failed: %s\n", m, t);
            }


        } catch (Exception e) {
            // not static method
            System.out.println("INVALID @Test: " + m);
        }
    }
}
```

```java
// Suite

public class SampleTestSuite {

    @Test
    public static void success1() {}

    @ExceptionTest(RuntimeException.class)
    public static void success2() {
        throw new RuntimeException();
    }

    @ExceptionTest(ArithmeticException.class)
    public static void success3() {
        int i = 0;
        int b = i / i;
    }

    @MultipleExceptionTest({
            IndexOutOfBoundsException.class,
            NullPointerException.class
    })
    public static void success4() {
        List<String> list = new ArrayList<>();

        list.addAll(5, null);
    }

    // failure tests
    @Test
    public void failure1() { throw new RuntimeException(); }

    @ExceptionTest(Exception.class)
    public static void failure2() {}

    @MultipleExceptionTest({
            IndexOutOfBoundsException.class,
            NullPointerException.class
    })
    public static void failure3() { }
}
```

```java
// output

INVALID @Test: public void com.github.lambda.annotations.SampleTestSuite.failure1()
ExceptionTest public static void com.github.lambda.annotations.SampleTestSuite.failure2() failed: No exception
MultipleExceptionTest public static void com.github.lambda.annotations.SampleTestSuite.failure3() failed: No exception
Passed: 4, Failed 3
```