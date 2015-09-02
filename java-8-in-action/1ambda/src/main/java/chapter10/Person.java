package chapter10;

import java.util.Optional;

public class Person {
    public Person(int age) {
        this.age = age;
        this.car = Optional.empty();
    }

    public Person(int age, Car car) {
        this.age = age;
        this.car = Optional.of(car);
    }

    private Optional<Car> car;
    private int age;
    public Optional<Car> getCar() { return car; }
    public int getAge() { return age; }

    public static Optional<String> getInsuranceName(Person p) {
        Optional<Person> optPerson = Optional.of(p);
        return optPerson
            .flatMap(Person::getCar)
            .flatMap(Car::getInsurance)
            .map(Insurance::getName);
    }

    public static String getCarInsuranceName(Optional<Person> optPerson, int minAge) {
//        return optPerson
//            .filter(p -> p.getAge() >= minAge)
//            .flatMap(Person::getInsuranceName)
//            .orElse("DEFAULT");

        return optPerson
            .filter(p -> p.getAge() >= minAge)
            .flatMap(Person::getCar)
            .flatMap(Car::getInsurance)
            .map(Insurance::getName)
            .orElse("DEFAULT");
    }
}

