package org.example.state.interfaces;

import org.example.individuals.Individual;

import java.util.Random;

// Interfejs Stanu
public interface HealthState {
    // Wywoływane w każdym kroku symulacji (np. do odliczania czasu choroby)
    void update(Individual context);

    // Czy ten osobnik może zarażać innych?
    boolean isInfectious();

    // Prawdopodobieństwo zarażenia kogoś (0.5 dla bezobjawowych, 1.0 dla objawowych)
    double getInfectionProbability();

    // Metoda pomocnicza do identyfikacji stanu (opcjonalna, dla wizualizacji)
    String getName();
}