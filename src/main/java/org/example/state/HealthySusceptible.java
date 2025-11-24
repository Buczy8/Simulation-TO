package org.example.state;

import org.example.individuals.Individual;
import org.example.state.interfaces.HealthState;
import java.util.Random;

public class HealthySusceptible implements HealthState {
    @Override
    public void update(Individual context) {
        // Zdrowy nic nie robi z czasem, po prostu sobie żyje
    }

    @Override
    public boolean isInfectious() {
        return false; // Zdrowy nie zaraża [cite: 13]
    }

    @Override
    public double getInfectionProbability() {
        return 0.0;
    }

    @Override
    public String getName() {
        return "Zdrowy (Podatny)";
    }
}
