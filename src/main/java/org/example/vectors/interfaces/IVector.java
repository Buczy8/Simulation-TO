package org.example.vectors.interfaces;

public interface IVector {
    public double abs();

    public double cdot(IVector parm);

    public double[] getComponents();
}
