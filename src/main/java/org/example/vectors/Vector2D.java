package org.example.vectors;

import org.example.vectors.interfaces.IVector;

public class Vector2D implements IVector {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double abs(){
        return Math.sqrt(x*x + y*y);
    }

    @Override
    public double cdot(IVector parm){
        return this.x * parm.getComponents()[0] + this.y * parm.getComponents()[1];
    }

    @Override
    public double[] getComponents(){
        return new double[]{this.x, this.y};
    }
}
