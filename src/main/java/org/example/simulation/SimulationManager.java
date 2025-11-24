package org.example.simulation;

import org.example.individuals.Individual;
import org.example.state.InfectedAsymptomatic;
import org.example.state.InfectedSymptomatic;
import org.example.state.HealthySusceptible;
import org.example.state.interfaces.HealthState;
import org.example.vectors.Vector2D;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager {
    // Ustawienia symulacji
    private final double WIDTH;
    private final double HEIGHT;
    private final int targetPopulation;
    private final int FPS = 25; //
    private SimulationGUI gui;

    private List<Individual> population;
    private Random random;
    private boolean isRunning;
    private int simulationStep = 0;

    public SimulationManager(double width, double height, int initialPopulation) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.targetPopulation = initialPopulation;
        this.population = new ArrayList<>();
        this.random = new Random();
        this.isRunning = false;

        // Inicjalizacja populacji początkowej
        for (int i = 0; i < initialPopulation; i++) {
            // Losowa pozycja startowa
            spawnIndividualAtRandomPosition();
        }
        this.gui = new org.example.simulation.SimulationGUI(this, width, height);
    }

    // --- GŁÓWNA PĘTLA PROGRAMU ---
    public void start() {
        isRunning = true;

        // Wątek symulacji, żeby nie blokował interfejsu (jeśli będziesz miał GUI)
        new Thread(() -> {
            while (isRunning) {
                long startTime = System.currentTimeMillis();

                // 1. Wykonaj logikę kroku
                step();

                SwingUtilities.invokeLater(() -> gui.refresh());

                // 2. Wypisz statystyki (dla podglądu w konsoli)
                if (simulationStep % 25 == 0) {
                    printStats();
                }

                // 3. Czekaj, aby utrzymać 25 klatek na sekundę
                long elapsedTime = System.currentTimeMillis() - startTime;
                long waitTime = (1000 / FPS) - elapsedTime;

                try {
                    if (waitTime > 0) Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
    }

    // --- LOGIKA POJEDYNCZEGO KROKU ---
    private void step() {
        simulationStep++;

        // 1. Ruch i usuwanie osobników
        Iterator<Individual> it = population.iterator();
        while (it.hasNext()) {
            Individual person = it.next();

            // Ruch + losowe zmiany prędkości + granice
            person.tick(WIDTH, HEIGHT);

            // Jeśli wyszedł -> usuwamy
            if (person.isRemoved()) {
                it.remove();
            }
        }

        // 2. Logika zakażania (skrócona dla czytelności - użyj pełnej z poprzednich odpowiedzi)
        handleInfections();

        // 3. UZUPEŁNIANIE POPULACJI (Nowa logika)
        // Sprawdzamy, ilu brakuje do stanu początkowego
        while (population.size() < targetPopulation) {
            spawnIndividualAtBoundary();
        }
    }
    private void spawnIndividualAtBoundary() {
        double x = 0, y = 0;

        // Losujemy ścianę: 0=Góra, 1=Prawo, 2=Dół, 3=Lewo
        int side = random.nextInt(4);

        // Przesunięcie 0.1, żeby nie zostali usunięci w tej samej klatce przez checkBoundaries
        switch (side) {
            case 0: // Góra
                x = random.nextDouble() * WIDTH;
                y = 0.1;
                break;
            case 1: // Prawo
                x = WIDTH - 0.1;
                y = random.nextDouble() * HEIGHT;
                break;
            case 2: // Dół
                x = random.nextDouble() * WIDTH;
                y = HEIGHT - 0.1;
                break;
            case 3: // Lewo
                x = 0.1;
                y = random.nextDouble() * HEIGHT;
                break;
        }

        // Część populacji może być odporna (np. 10% szansy na wrodzoną odporność)
        boolean isImmune = random.nextDouble() < 0.1;

        // Tworzymy nowego osobnika
        Individual newcomer = new Individual(x, y, isImmune);

        // --- NOWA LOGIKA ZAKAŻANIA PRZY WEJŚCIU  ---
        // Jeśli osobnik nie jest odporny, ma 10% szans na bycie zakażonym
        if (!isImmune && random.nextDouble() < 0.1) {
            // Metoda tryInfect() losuje wewnątrz, czy będzie to:
            // - InfectedSymptomatic (Objawowy)
            // - albo InfectedAsymptomatic (Bezobjawowy)
            newcomer.tryInfect();
        }
        // ----------------------------------------------------

        population.add(newcomer);
    }

    // --- Metoda pomocnicza dla startowej populacji (po całym obszarze) ---
    private void spawnIndividualAtRandomPosition() {
        double x = random.nextDouble() * WIDTH;
        double y = random.nextDouble() * HEIGHT;
        boolean isImmune = random.nextDouble() < 0.1;
        population.add(new Individual(x, y, isImmune));
    }
    // --- LOGIKA INFEKCJI [cite: 18-19] ---
    private void handleInfections() {
        for (Individual person : population) {
            // Jeśli osoba nie jest podatna, pomijamy ją w tej pętli jako "biorcę"
            if (!(person.getState() instanceof HealthySusceptible)) {
                person.resetContactTime(); // Reset licznika, jeśli nie jest podatny
                continue;
            }

            boolean exposed = false;
            double highestInfectionProb = 0.0;

            // Sprawdzamy sąsiadów
            for (Individual neighbor : population) {
                if (person == neighbor) continue;

                // Sprawdzamy stan sąsiada
                HealthState neighborState = neighbor.getState();

                // Jeśli sąsiad zaraża (jest zakażony)
                if (neighborState.isInfectious()) {
                    // Sprawdzamy dystans
                    if (person.getPosition().distance(neighbor.getPosition()) <= 2.0) {
                        exposed = true;
                        // Zapamiętujemy "siłę" wirusa (50% lub 100%) [cite: 19]
                        highestInfectionProb = Math.max(highestInfectionProb, neighborState.getInfectionProbability());
                    }
                }
            }

            if (exposed) {
                // Inkrementacja licznika kontaktu
                person.incrementContactTime();

                // Sprawdzenie warunku czasu (>= 3s)
                if (person.hasContactLastedLongEnough()) {
                    // Próba zakażenia z wyliczonym prawdopodobieństwem
                    if (random.nextDouble() < highestInfectionProb) {
                        person.tryInfect();
                        person.resetContactTime(); // Po zakażeniu resetujemy licznik
                    }
                }
            } else {
                // Jeśli nie ma nikogo w pobliżu, licznik się zeruje (ciągłość kontaktu przerwana)
                person.resetContactTime();
            }
        }
    }

    // --- DODAWANIE NOWYCH OSOBNIKÓW [cite: 8] ---
    private void addNewIndividuals() {
        // Losujemy ile osób wchodzi (np. 1 lub 0)
        if (random.nextBoolean()) return;

        // Losujemy punkt na granicy
        double x, y;
        if (random.nextBoolean()) { // Ściany pionowe
            x = random.nextBoolean() ? 0.1 : WIDTH - 0.1;
            y = random.nextDouble() * HEIGHT;
        } else { // Ściany poziome
            x = random.nextDouble() * WIDTH;
            y = random.nextBoolean() ? 0.1 : HEIGHT - 0.1;
        }

        // Tworzymy nowego osobnika
        // Losujemy czy odporny (zakładam małą szansę np. 5%)
        boolean isImmune = random.nextDouble() < 0.05;
        Individual newcomer = new Individual(x, y, isImmune);

        // Szansa 10% na bycie zakażonym przy wejściu [cite: 9]
        if (!isImmune && random.nextDouble() < 0.10) {
            newcomer.tryInfect();
        }

        population.add(newcomer);
    }

    // Metoda pomocnicza do debugowania
    private void printStats() {
        long healthy = population.stream().filter(i -> i.getState() instanceof HealthySusceptible).count();
        long infected = population.stream().filter(i -> i.getState().isInfectious()).count();
        long immune = population.stream().filter(i -> i.getState().getName().equals("Odporny")).count();

        System.out.println("Krok: " + simulationStep +
                " | Populacja: " + population.size() +
                " | Zdrowi: " + healthy +
                " | Zakażeni: " + infected +
                " | Odporni: " + immune);
    }

    // Getter do Memento (później się przyda)
    public List<Individual> getPopulation() {
        return population;
    }
}
