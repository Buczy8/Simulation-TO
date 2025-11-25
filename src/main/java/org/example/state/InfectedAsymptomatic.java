package org.example.state;

import org.example.state.interfaces.HealthState;

public class InfectedAsymptomatic extends InfectedState {

    public InfectedAsymptomatic() {
        super();
    }

    public InfectedAsymptomatic(int infectionTimer, int recoveryTime) {
        super(infectionTimer, recoveryTime);
    }

    @Override
    public boolean isInfectious() {
        return true;
    } // może zarażać

    @Override
    public double getInfectionProbability() {
        return 0.5; // 50% szans na zarażenie
    }

    @Override
    public String getName() {
        return "Zakażony (Bezobjawowy)";
    }

    // Zwraca kopię tego samego stanu
    @Override
    public HealthState copy() {
        // Zwracamy kopię z TYM SAMYM stanem licznika
        return new InfectedAsymptomatic(this.infectionTimer, this.recoveryTime);
    }
}
