package org.example.simulation;

import org.example.individuals.Individual;
import org.example.state.HealthySusceptible;
import org.example.state.Immune;
import org.example.state.interfaces.HealthState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager {
    // Ustawienia symulacji
    private final double WIDTH;
    private final double HEIGHT;
    private final int targetPopulation; // Liczba populacji
    private final int FPS = 25;
    private SimulationGUI gui;

    private List<Individual> population;
    private Random random;
    private boolean isRunning;
    private int simulationStep = 0;
    private final boolean hasImmunityEnabled; // Pole do kontrolowania odporności

    public SimulationManager(double width, double height, int initialPopulation, boolean initialImmunity) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.targetPopulation = initialPopulation;
        this.hasImmunityEnabled = initialImmunity;
        this.population = new ArrayList<>();
        this.random = new Random();
        this.isRunning = false;

        // Inicjalizacja populacji początkowej
        for (int i = 0; i < initialPopulation; i++) {
            spawnIndividualAtRandomPosition();
        }
        this.gui = new SimulationGUI(this, width, height);
    }

    // Głowna pętla
    public void start() {
        isRunning = true;

        // Uruchomienie wątku symulacji
        new Thread(() -> {
            while (isRunning) {
                long startTime = System.currentTimeMillis(); // zapisanie rozpoczęcia symulacji

                step(); // krok

                // odświeżamy wizualizację rozprzestrzeniania się choroby
                SwingUtilities.invokeLater(() -> gui.refresh());

                // Co 25 (1 sekunda) wyświetlamy stan populacji
                if (simulationStep % 25 == 0) {
                    printStats();
                }


                long elapsedTime = System.currentTimeMillis() - startTime; // czas trwania symulacji
                long waitTime = (1000 / FPS) - elapsedTime; // czas do kolejnych 25 kroków

                try {
                    if (waitTime > 0) Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Pojedyńczy krok
    private void step() {
        simulationStep++; // zwiększamy licznik kroków

        // Wykonanie kroku dla każdego osobnika z populacji
        Iterator<Individual> it = population.iterator();
        while (it.hasNext()) {
            Individual person = it.next();
            person.tick(WIDTH, HEIGHT);
            if (person.isRemoved()) {
                it.remove();
            }
        }

        // Możliwe zakażanie
        handleInfections();

        // Uzupełnianie populacji
        while (population.size() < targetPopulation) {
            spawnIndividualAtBoundary();
        }
    }

    // Funkcja do tworzenia nowych osobników na granicy
    private void spawnIndividualAtBoundary() {
        double x = 0, y = 0;
        int side = random.nextInt(4); //losujemy ścianę

        // Przesunięcie o 0.1, żeby nie zostali usunięci w tej samej klatce przez checkBoundaries
        switch (side) {
            case 0: x = random.nextDouble() * WIDTH; y = 0.1; break;
            case 1: x = WIDTH - 0.1; y = random.nextDouble() * HEIGHT; break;
            case 2: x = random.nextDouble() * WIDTH; y = HEIGHT - 0.1; break;
            case 3: x = 0.1; y = random.nextDouble() * HEIGHT; break;
        }

        //  Odporność zależy od ustawienia w konstruktorze, jeżeli odporność jest włączona osobnik ma 10% szans na odporność
        boolean isImmune = hasImmunityEnabled && random.nextDouble() < 0.1;
        Individual newcomer = new Individual(x, y, isImmune);

        // Możliwe zakażanie
        if (!isImmune && random.nextDouble() < 0.1) {
            newcomer.tryInfect();
        }

        // dodanie osobnika do populacji
        population.add(newcomer);
    }

    // --- Metoda pomocnicza dla startowej populacji
    private void spawnIndividualAtRandomPosition() {
        double x = random.nextDouble() * WIDTH;
        double y = random.nextDouble() * HEIGHT;
        boolean isImmune = hasImmunityEnabled && random.nextDouble() < 0.1;
        population.add(new Individual(x, y, isImmune));
    }

    // logika infekcji
    private void handleInfections() {
        for (Individual person : population) {
            // Jeśli osoba nie jest podatna, pomijamy ją w tej pętli jako "biorcę"
            if (!(person.getState() instanceof HealthySusceptible)) {
                person.resetContactTime();
                continue;
            }

            boolean exposed = false;
            double highestInfectionProb = 0.0;

            // Sprawdzamy sąsiadów
            for (Individual neighbor : population) {
                if (person == neighbor) continue;

                // Sprawdzamy stan sąsiada
                HealthState neighborState = neighbor.getState();

                // Jeśli sąsiad zaraża
                if (neighborState.isInfectious()) {
                    // Sprawdzamy dystans
                    if (person.getPosition().distance(neighbor.getPosition()) <= 2.0) {
                        exposed = true;
                        // Zapamiętujemy siłę wirusa
                        highestInfectionProb = Math.max(highestInfectionProb, neighborState.getInfectionProbability());
                    }
                }
            }

            if (exposed) {
                // Inkrementacja licznika kontaktu
                person.incrementContactTime();

                // Sprawdzenie warunku czasu
                if (person.hasContactLastedLongEnough()) {
                    // Próba zakażenia z wyliczonym prawdopodobieństwem
                    if (random.nextDouble() < highestInfectionProb) {
                        person.tryInfect();
                        person.resetContactTime();
                    }
                }
            } else {
                // Jeśli nie ma nikogo w pobliżu, licznik się zeruje
                person.resetContactTime();
            }
        }
    }
    // Funkcja pomocnicza do wyświetlania statystyk
    private void printStats() {
        long healthy = population.stream().filter(i -> i.getState() instanceof HealthySusceptible).count();
        long infected = population.stream().filter(i -> i.getState().isInfectious()).count();
        long immune = population.stream().filter(i -> i.getState() instanceof Immune).count();

        System.out.println("Krok: " + simulationStep +
                " | Populacja: " + population.size() +
                " | Zdrowi: " + healthy +
                " | Zakażeni: " + infected +
                " | Odporni: " + immune);
    }

    public List<Individual> getPopulation() {
        return population;
    }
}
