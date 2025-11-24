package org.example.vectors;

public class Polar2DInheritance extends Vector2D {

    public Polar2DInheritance(double x, double y) {
        super(x, y);
    }

    public double getAngle() {
        return Math.atan2(super.getComponents()[1], super.getComponents()[0]);
    }

}
