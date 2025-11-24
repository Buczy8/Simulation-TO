package org.example.simulation;

import org.example.individuals.Individual;
import org.example.simulation.SimulationManager;
import org.example.state.*;
import org.example.state.interfaces.HealthState;
import org.example.vectors.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationGUI extends JFrame {
    private final SimulationPanel panel;
    private final SimulationManager manager;

    public SimulationGUI(SimulationManager manager, double width, double height) {
        this.manager = manager;
        // Ustawienia okna
        setTitle("Symulacja Wirusa - Lab 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Obliczamy rozmiar okna (np. 10 pikseli na 1 metr)
        int scale = 10;
        int windowWidth = (int) (width * scale);
        int windowHeight = (int) (height * scale);

        this.panel = new SimulationPanel(width, height, scale);
        this.panel.setPreferredSize(new Dimension(windowWidth, windowHeight));

        add(this.panel);
        pack();
        setLocationRelativeTo(null); // Wyśrodkowanie
        setVisible(true);
    }

    // Metoda wywoływana z pętli symulacji, żeby odświeżyć ekran
    public void refresh() {
        panel.repaint();
    }

    // Wewnętrzna klasa panelu rysującego
    private class SimulationPanel extends JPanel {
        private final double simWidth;
        private final double simHeight;
        private final int scale;

        public SimulationPanel(double simWidth, double simHeight, int scale) {
            this.simWidth = simWidth;
            this.simHeight = simHeight;
            this.scale = scale;
            this.setBackground(Color.DARK_GRAY); // Tło planszy
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Wygładzanie krawędzi (antyaliasing) - ładniejsze kropki
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Pobieramy listę osobników z managera
            // UWAGA: Kopiujemy listę lub używamy bezpiecznej iteracji,
            // bo symulacja może ją modyfikować w innym wątku!
            List<Individual> population = manager.getPopulation();

            // Żeby uniknąć błędu ConcurrentModificationException,
            // w prostej wersji możemy użyć zwykłej pętli for (lub skopiować listę w managerze)
            for (int i = 0; i < population.size(); i++) {
                try {
                    Individual ind = population.get(i);
                    drawIndividual(g2d, ind);
                } catch (IndexOutOfBoundsException e) {
                    // Ignorujemy momenty, gdy ktoś zniknął w trakcie rysowania
                }
            }
        }

        private void drawIndividual(Graphics2D g, Individual ind) {
            Vector2D pos = ind.getPosition();
            double[] coords = pos.getComponents();

            // Skalowanie: Metry -> Piksele
            int x = (int) (coords[0] * scale);
            int y = (int) (coords[1] * scale);
            int size = 8; // Wielkość kropki w pikselach

            // Dobór koloru na podstawie stanu [cite: 10-17]
            HealthState state = ind.getState();
            if (state instanceof HealthySusceptible) {
                g.setColor(Color.GREEN);
            } else if (state instanceof InfectedSymptomatic) {
                g.setColor(Color.RED);
            } else if (state instanceof InfectedAsymptomatic) {
                g.setColor(Color.ORANGE); // Zółty/Pomarańczowy dla bezobjawowych
            } else if (state.getName().equals("Odporny")) { // Lub instanceof Immune
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.WHITE);
            }

            // Rysowanie kropki (centrowanie x,y)
            g.fillOval(x - size / 2, y - size / 2, size, size);
        }
    }
}
