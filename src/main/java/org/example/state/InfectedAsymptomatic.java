package org.example.state;

public class InfectedAsymptomatic extends InfectedState {
    @Override
    public boolean isInfectious() {
        return true;
    }

    @Override
    public double getInfectionProbability() {
        return 0.5; // 50% szans na zarażenie [cite: 19]
    }

    @Override
    public String getName() {
        return "Zakażony (Bezobjawowy)";
    }
}
