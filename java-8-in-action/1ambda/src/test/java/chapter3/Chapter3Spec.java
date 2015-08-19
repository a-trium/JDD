package chapter3;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static chapter3.FunctionalInterfaces.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class Chapter3Spec {

    List<Integer> ns = Arrays.asList(1, 2, 3, 4);

    @Test
    public void test_filter() {
        List<Integer> even = filter(ns, n -> n % 2 == 0);
        for(int n : even) assertTrue(n % 2 == 0);
    }

    @Test
    public void test_forEach() {
        forEach(ns, n -> System.out.println(n));
    }

    @Test
    public void test_map() {
        List<Integer> doubled = map(ns, n -> n * 2);
        assertEquals(doubled, Arrays.asList(2, 4, 6, 8));
    }
}
