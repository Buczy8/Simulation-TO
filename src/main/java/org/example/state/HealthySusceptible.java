package org.example.state;

import org.example.individuals.Individual;
import org.example.state.interfaces.HealthState;

public class HealthySusceptible implements HealthState {
    @Override
    public void update(Individual context) {
        // Zdrowy nic nie robi po prostu sobie żyje
    }

    @Override
    public boolean isInfectious() {
        return false; // Zdrowy nie zaraża
    }

    @Override
    public double getInfectionProbability() {
        return 0.0;
    } // zdrowy nie zaraża

    @Override
    public String getName() {
        return "Zdrowy (Podatny)";
    }
}
