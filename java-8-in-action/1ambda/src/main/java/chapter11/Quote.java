package chapter11;

public class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }


    public Quote(String shopName, double price, Discount.Code code) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = code;
    }

    public static Quote parse(String s) {
        String[] splited = s.split(":");
        String shopName = splited[0];
        double price = Double.parseDouble(splited[1]);
        Discount.Code code = Discount.Code.valueOf(splited[2]);

        return new Quote(shopName, price, code);
    }
}
