package org.example.vectors;

import org.example.vectors.interfaces.IVector;

public class Vector3DInheritance extends Vector2D {
    private double z;

    public Vector3DInheritance(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public double abs() {
        return Math.sqrt(super.abs() * super.abs() + z * z);
    }

    @Override
    public double cdot(IVector parm) {
        double[] paramComponents = parm.getComponents();
        double[] thisComponents = this.getComponents();

        if (paramComponents.length == 2) {
            return thisComponents[0] * paramComponents[0] + thisComponents[1] * paramComponents[1];
        } else if (paramComponents.length == 3) {
            return thisComponents[0] * paramComponents[0] + thisComponents[1] * paramComponents[1] + thisComponents[2] * paramComponents[2];
        }
        throw new IllegalArgumentException("Unsupported vector dimension for cdot operation.");
    }

    public double[] getComponents() {
        return new double[]{super.getComponents()[0], super.getComponents()[1], this.z};
    }

    public IVector getSrcV(){
        return new Vector2D(super.getComponents()[0], super.getComponents()[1]);
    }
    
    public Vector3DInheritance cross(IVector param) {
        double[] paramComponents = param.getComponents();
        double[] thisComponents = this.getComponents();

        double paramZ = (paramComponents.length > 2) ? paramComponents[2] : 0.0;

        double newX = thisComponents[1] * paramZ - thisComponents[2] * paramComponents[1];
        double newY = thisComponents[2] * paramComponents[0] - thisComponents[0] * paramZ;
        double newZ = thisComponents[0] * paramComponents[1] - thisComponents[1] * paramComponents[0];

        return new Vector3DInheritance(newX, newY, newZ);
    }
}
