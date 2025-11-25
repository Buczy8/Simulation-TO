package org.example.simulation.interfaces;

public interface SimulationListener {
    void onSimulationStep(); // Odświeża widok
    void onStatsUpdated(long step, int populationSize, long healthy, long infected, long immune); // Pokaż statystyki
}
