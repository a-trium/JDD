package chapter10;

import java.util.Optional;

public class Car {

    public Car() { insurance = Optional.empty(); }
    public Car(Insurance insurance) { this.insurance = Optional.of(insurance); }

    private Optional<Insurance> insurance;
    public Optional<Insurance> getInsurance() { return insurance; }
}
