package org.example.simulation;

import org.example.individuals.Individual;
import org.example.memento.Caretaker;
import org.example.memento.Memento;
import org.example.simulation.interfaces.SimulationListener;
import org.example.state.Immune;

import java.util.ArrayList;
import java.util.List;

public class SimulationManager {
    private final int FPS = 25;

    // serwisy potrzebne do działania
    private final PopulationService populationService;
    private final InfectionService infectionService;
    private final Caretaker caretaker;

    // Abstrakcja widoku
    private SimulationListener listener;

    private boolean isRunning;
    private int stepCounter = 0;

    public SimulationManager(double width, double height, int startPop, boolean immunity) {
        this.populationService = new PopulationService(width, height, startPop, immunity);
        this.infectionService = new InfectionService();
        this.caretaker = new Caretaker();
    }

    // Rejestracja GUI
    public void setListener(SimulationListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;

        new Thread(() -> {
            while (isRunning) {
                long start = System.currentTimeMillis();

                step(); // Logika kroku

                // Powiadomienie widoku o zmianie
                if (listener != null) {
                    listener.onSimulationStep();

                    // Wyświetlanie statystyk po 25 krokach
                    if (stepCounter % 25 == 0) {
                        calculateAndNotifyStats();
                    }
                }

                long wait = (1000 / FPS) - (System.currentTimeMillis() - start); // czas do następnych kroków
                try { if (wait > 0) Thread.sleep(wait); } catch (InterruptedException e) {}
            }
        }).start();
    }

    private void step() {
        synchronized (this) {
            stepCounter++;

            // Ruch i czyszczenie
            populationService.updateMovement();

            // Pobieramy listę, bo to ten sam wątek
            infectionService.handleInfections(populationService.getPopulationRaw());

            // Uzupełnianie populacji
            populationService.maintainPopulation();
        }
    }

    // zapisywanie stanu do caretaker
    public void saveState() {
        synchronized (this) {
            // Pobieramy stan z serwisu populacji
            List<Individual> snapshot = populationService.getSafeCopy();
            caretaker.saveMemento(new Memento(snapshot, stepCounter));
        }
    }

    // Wczytywanie stanu z caretaker
    public void loadState() {
        synchronized (this) {
            Memento m = caretaker.getMemento();
            if (m != null) {
                this.stepCounter = m.getStep();

                // Odtwarzanie głębokiej kopii
                List<Individual> restored = new ArrayList<>();
                for (Individual ind : m.getState()) {
                    restored.add(ind.deepCopy());
                }

                // Wstrzyknięcie stanu do serwisu
                populationService.setPopulation(restored);

                if (listener != null) listener.onSimulationStep();
            }
        }
    }

    // Delegacja pobierania danych dla GUI
    public List<Individual> getPopulationForGUI() {
        synchronized (this) {
            return populationService.getSafeCopy();
        }
    }

    // obliczanie danych do statystyki
    private void calculateAndNotifyStats() {
        List<Individual> pop = populationService.getPopulationRaw();
        long healthy = pop.stream().filter(i -> i.getState().isSusceptible()).count();
        long infected = pop.stream().filter(i -> i.getState().isInfectious()).count();
        long immune = pop.stream().filter(i -> i.getState() instanceof Immune).count();

        listener.onStatsUpdated(stepCounter, pop.size(), healthy, infected, immune);
    }
}
