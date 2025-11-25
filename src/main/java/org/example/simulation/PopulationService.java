package org.example.simulation;

import org.example.individuals.Individual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PopulationService {
    private List<Individual> population;
    private final double width;
    private final double height;
    private final int targetPopulation;
    private final boolean immunityEnabled;
    private final Random random;

    public PopulationService(double width, double height, int targetPopulation, boolean immunityEnabled) {
        this.width = width;
        this.height = height;
        this.targetPopulation = targetPopulation;
        this.immunityEnabled = immunityEnabled;
        this.population = new ArrayList<>();
        this.random = new Random();

        // Inicjalizacja
        for (int i = 0; i < targetPopulation; i++) {
            spawnRandom();
        }
    }

    // Metoda wywoływana 25 razy na sekundę
    public void updateMovement() {
        Iterator<Individual> it = population.iterator();
        while (it.hasNext()) {
            Individual person = it.next();
            person.tick(width, height);
            if (person.isRemoved()) {
                it.remove();
            }
        }
    }

    // dodawanie osobników do populacji
    public void maintainPopulation() {
        while (population.size() < targetPopulation) {
            spawnAtBoundary();
        }
    }

    // Dodanie osobnika na granicy (prawie)
    private void spawnAtBoundary() {
        double x = 0, y = 0;
        int side = random.nextInt(4);
        switch (side) {
            case 0 -> { x = random.nextDouble() * width; y = 0.1; }
            case 1 -> { x = width - 0.1; y = random.nextDouble() * height; }
            case 2 -> { x = random.nextDouble() * width; y = height - 0.1; }
            case 3 -> { x = 0.1; y = random.nextDouble() * height; }
        }
        createAndAdd(x, y);
    }

    // Dodanie osobnika w losowym punkcie na obszarze
    private void spawnRandom() {
        createAndAdd(random.nextDouble() * width, random.nextDouble() * height);
    }

    // Dodanie osobnika do populacji
    private void createAndAdd(double x, double y) {
        boolean isImmune = immunityEnabled && random.nextDouble() < 0.1;
        Individual newcomer = new Individual(x, y, isImmune);
        // Logika zakażania przy wejściu
        if (!isImmune && random.nextDouble() < 0.1) {
            newcomer.tryInfect();
        }
        population.add(newcomer);
    }

    // Zwraca całą populację osobników
    public List<Individual> getPopulationRaw() { return population; }

    // bezpieczna kopia populacji
    public synchronized List<Individual> getSafeCopy() {
        return new ArrayList<>(population);
    }

    // Dla wczytywania stanu
    public void setPopulation(List<Individual> newPopulation) {
        this.population = newPopulation;
    }
}
