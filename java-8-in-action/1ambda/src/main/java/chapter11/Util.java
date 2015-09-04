package chapter11;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Util {
    private static final Random generator = new Random();

    public static void delay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static long randomDelay() {
        long delay = generator.nextInt(1500);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return delay;
    }

    private static final DecimalFormat formatter = new DecimalFormat(
        "#.##", new DecimalFormatSymbols(Locale.US));

    public static double format(double number) {
        synchronized (formatter) {
            return new Double(formatter.format(number));
        }
    }
}
