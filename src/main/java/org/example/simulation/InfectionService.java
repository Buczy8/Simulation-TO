package org.example.simulation;

import org.example.individuals.Individual;
import org.example.state.interfaces.HealthState;

import java.util.List;
import java.util.Random;

public class InfectionService {
    private final Random random = new Random();

    public void handleInfections(List<Individual> population) {
        for (Individual person : population) {
            // Logika pomijania
            if (!person.getState().isSusceptible()) {
                person.resetContactTime();
                continue;
            }

            boolean exposed = false;
            double highestProb = 0.0;

            // Szukanie sąsiadów
            for (Individual neighbor : population) {
                if (person == neighbor) continue;

                HealthState nState = neighbor.getState(); // Stan sąsiada
                if (nState.isInfectious()) {
                    // Logika zarażenia
                    if (person.getPosition().distance(neighbor.getPosition()) <= 2.0) {
                        exposed = true;
                        highestProb = Math.max(highestProb, nState.getInfectionProbability()); // prawdopodobieństwo zarażenia
                    }
                }
            }

            // Aplikacja skutków
            if (exposed) {
                person.incrementContactTime();
                if (person.hasContactLastedLongEnough()) {
                    if (random.nextDouble() < highestProb) {
                        person.tryInfect();
                        person.resetContactTime();
                    }
                }
            } else {
                person.resetContactTime();
            }
        }
    }
}
