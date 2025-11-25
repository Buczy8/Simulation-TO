package org.example;

import org.example.simulation.SimulationManager;

public class Main {
    public static void main(String[] args) {

        // Aby wyłączyć odporność zmienić na false
        SimulationManager sim = new SimulationManager(100, 70, 1000, false);
        sim.start();
    }
}
