package chapter9;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Chapter9Spec {

    @Test
    public void test_default_method_inheritance() {
        assertEquals("B extends A", new C().getName());
    }

}


