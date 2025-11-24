package org.example;

import org.example.simulation.SimulationManager;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SimulationManager sim = new SimulationManager(50, 50, 100);
        sim.start();
    }
}
