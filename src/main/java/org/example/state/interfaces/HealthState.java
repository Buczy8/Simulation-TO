package org.example.state.interfaces;

import org.example.individuals.Individual;


public interface HealthState {
    // Wywoływane w każdym kroku symulacji
    void update(Individual context);

    // Czy osobnik może zarażać innych
    boolean isInfectious();

    // Prawdopodobieństwo zarażenia kogoś
    double getInfectionProbability();

    // Metoda pomocnicza do identyfikacji stanu
    String getName();

    // Kopiowanie stanu
    HealthState copy();
}