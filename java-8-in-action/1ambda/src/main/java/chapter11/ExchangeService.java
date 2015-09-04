package chapter11;

public class ExchangeService {

    public enum Money {
        USD, FRANC
    }

    public static double getRate(Money m1, Money m2) {
        Util.delay();
        return 1.5;
    }
}
