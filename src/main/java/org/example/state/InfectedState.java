package org.example.state;

import org.example.individuals.Individual;
import org.example.state.interfaces.HealthState;

import java.util.Random;


abstract class InfectedState implements HealthState {
    protected int infectionTimer = 0; // Licznik kroków
    protected final int recoveryTime; // Cel (kiedy wyzdrowieje)
    private static final int STEPS_PER_SECOND = 25; //

    public InfectedState() {
        Random rand = new Random();
        // Czas trwania: 20 do 30 sekund
        // Przeliczamy sekundy na kroki:
        int minSteps = 20 * STEPS_PER_SECOND; // 500
        int maxSteps = 30 * STEPS_PER_SECOND; // 750

        // Losujemy liczbę kroków z przedziału [500, 750]
        this.recoveryTime = minSteps + rand.nextInt(maxSteps - minSteps + 1);
    }

    @Override
    public void update(Individual context) {
        // Inkrementacja licznika w każdym kroku
        infectionTimer++;

        // Sprawdzenie, czy czas choroby minął
        if (infectionTimer >= recoveryTime) {
            // Zmiana stanu na Odporny
            context.setState(new Immune());
        }
    }

    // Metody abstrakcyjne, które muszą zaimplementować klasy pochodne
    @Override
    public abstract boolean isInfectious();

    @Override
    public abstract double getInfectionProbability();

    @Override
    public abstract String getName();
}
