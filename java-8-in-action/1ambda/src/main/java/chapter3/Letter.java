package chapter3;

public class Letter {
    public static String addHeader(String text) {
        return "header" + text;
    }

    public static String addFooter(String text) {
        return text + "footer";
    }
}
