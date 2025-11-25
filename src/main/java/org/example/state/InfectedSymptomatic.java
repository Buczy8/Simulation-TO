package org.example.state;

import org.example.state.interfaces.HealthState;

public class InfectedSymptomatic extends InfectedState {
    public InfectedSymptomatic() {
        super(); // Wywołuje losowanie czasu z InfectedState()
    }

    // Konstruktor z parametrami
    public InfectedSymptomatic(int infectionTimer, int recoveryTime) {
        super(infectionTimer, recoveryTime);
    }

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

    @Override
    public HealthState copy() {
        // Zwracamy kopię z TYM SAMYM stanem licznika
        return new InfectedSymptomatic(this.infectionTimer, this.recoveryTime);
    }
}
