package chapter9;

public class C extends D implements B, A {
}

interface A {
    default String getName() {
        return "A";
    }
}

interface B extends A {
    default String getName() {
        return "B extends A";
    }
}

class D implements A {}
