package org.example.vectors;

import org.example.vectors.interfaces.IPolar2D;
import org.example.vectors.interfaces.IVector;

public class Polar2DAdapter implements IPolar2D, IVector {
    private Vector2D srcVector;

    public Polar2DAdapter(Vector2D srcVector) {
        this.srcVector = srcVector;
    }

    @Override
    public double getAngle() {
        return Math.atan2(srcVector.getComponents()[1], srcVector.getComponents()[0]);
    }

    @Override
    public double abs() {
        return srcVector.abs();
    }

    @Override
    public double cdot(IVector parm) {
        return srcVector.cdot(parm);
    }

    @Override
    public double[] getComponents() {
        return srcVector.getComponents();
    }

}
