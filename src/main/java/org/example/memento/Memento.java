package org.example.memento;

import org.example.individuals.Individual;

import java.util.ArrayList;
import java.util.List;

public class Memento {
    private final List<Individual> populationState;
    private final int step;

    public Memento(List<Individual> population, int step) {
        this.step = step;
        this.populationState = new ArrayList<>();
        // Robimy kopię listy
        for (Individual ind : population) {
            this.populationState.add(ind.deepCopy());
        }
    }

    // Zwraca listę osobników
    public List<Individual> getState() {
        return populationState;
    }

    // Zwraca krok
    public int getStep() {
        return step;
    }
}
