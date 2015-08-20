package chapter3;

public class Apple {

    private int weight = 0;
    private String color = "default";

    public Apple() {}
    public Apple(int weight) { this.weight = weight; }
    public Apple(int weight, String color) { this.color = color; this.weight = weight; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
