package org.example.vectors;

import org.example.vectors.interfaces.IVector;

import java.util.Random;

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

    public Vector2D add(IVector other) {
        double[] comps = other.getComponents();
        return new Vector2D(this.x + comps[0], this.y + comps[1]);
    }

    /**
     * Oblicza odległość euklidesową do innego wektora.
     * Potrzebne do sprawdzania, czy odległość między osobnikami < 2m.
     */
    public double distance(IVector other) {
        double[] comps = other.getComponents();
        double dx = this.x - comps[0];
        double dy = this.y - comps[1];
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Metoda statyczna do generowania losowej prędkości.
     * Wymaganie: losowy kierunek i szybkość nie większa niż 2.5.
     */
    public static Vector2D randomVelocity(double maxSpeed) {
        Random rand = new Random();
        // Losowy kąt w radianach (0 do 2PI)
        double angle = rand.nextDouble() * 2 * Math.PI;
        // Losowa długość wektora (szybkość) od 0 do maxSpeed
        double speed = rand.nextDouble() * maxSpeed;

        // Konwersja ze współrzędnych biegunowych na kartezjańskie
        double vx = speed * Math.cos(angle);
        double vy = speed * Math.sin(angle);

        return new Vector2D(vx, vy);
    }

    // Gettery dla wygody (opcjonalne, ale przydatne przy sprawdzaniu granic)
    public double getX() { return x; }
    public double getY() { return y; }
}
