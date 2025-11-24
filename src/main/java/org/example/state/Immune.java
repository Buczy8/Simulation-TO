package org.example.state;

import org.example.individuals.Individual;
import org.example.state.interfaces.HealthState;

public class Immune implements HealthState {
    @Override
    public void update(Individual context) {
        // Odporny jest bezpieczny na zawsze
    }

    @Override
    public boolean isInfectious() {
        return false;
    }

    @Override
    public double getInfectionProbability() {
        return 0.0;
    }

    @Override
    public String getName() { return "Odporny"; }
}
