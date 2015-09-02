package chapter10;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Chapter10Spec {

    private Person rich, poor, nobody;
    private final String INSURANCE_NAME = "direct";

    @Before
    public void setUp() {
        Insurance insurance = new Insurance(INSURANCE_NAME);
        Car expensiveCar = new Car(insurance);
        rich = new Person(27, expensiveCar);
        poor = new Person(27);
    }

    @Test
    public void test_Optional_empty() {
        Optional<Car> emptyCar = Optional.empty();
        assertEquals(false, emptyCar.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void test_Optional_of_NPE() {
        Optional<Car> optCar = Optional.of(null);
    }

    @Test
    public void test_Optional_ofNullable() {
        Optional<Car> optCar = Optional.ofNullable(null);
        assertEquals(Optional.empty(), optCar);
    }

    @Test
    public void test_Optional_flatMap() {
        assertEquals(Optional.of(INSURANCE_NAME), Person.getInsuranceName(rich));
        assertEquals(Optional.empty(), Person.getInsuranceName(poor));
    }

    @Test
    public void test_getInsuranceName() {
        assertEquals(INSURANCE_NAME, Person.getCarInsuranceName(Optional.of(rich), 27));
        assertEquals(INSURANCE_NAME, Person.getCarInsuranceName(Optional.of(rich), 25));
        assertEquals("DEFAULT", Person.getCarInsuranceName(Optional.of(poor), 20));
    }
}


