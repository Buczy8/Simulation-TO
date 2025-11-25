package org.example;

import org.example.simulation.SimulationGUI;
import org.example.simulation.SimulationManager;

public class Main {
    public static void main(String[] args) {

        // Aby wyłączyć odporność zmienić na false
        SimulationManager manager = new SimulationManager(100, 60, 500, false);
        SimulationGUI gui = new SimulationGUI(manager, 100, 60);
        manager.setListener(gui); // Podpięcie
        manager.start();
    }
}
