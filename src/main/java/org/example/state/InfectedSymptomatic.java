package org.example.state;

public class InfectedSymptomatic extends InfectedState {
    @Override
    public boolean isInfectious() {
        return true; // może zarażać
    }

    @Override
    public double getInfectionProbability() {
        return 1.0; // 100% szans na zarażenie
    }

    @Override
    public String getName() {
        return "Zakażony (Objawowy)";
    }
}
