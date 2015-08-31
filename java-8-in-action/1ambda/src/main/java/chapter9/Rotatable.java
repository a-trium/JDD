package chapter9;

public interface Rotatable {
    void setRotationAngle(int angleInDegrees);

    int getRotationAngle();

    default void rotateBy(int angle) {
        setRotationAngle((getRotationAngle() + angle) % 360);
    }
}
