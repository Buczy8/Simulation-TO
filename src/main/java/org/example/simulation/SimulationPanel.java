package org.example.simulation;

import org.example.individuals.Individual;
import org.example.state.HealthySusceptible;
import org.example.state.Immune;
import org.example.state.InfectedAsymptomatic;
import org.example.state.InfectedSymptomatic;
import org.example.state.interfaces.HealthState;
import org.example.vectors.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationPanel extends JPanel {
    private final double simWidth;
    private final double simHeight;
    private final int scale;
    private final SimulationManager manager;

    public SimulationPanel(double simWidth, double simHeight, int scale, SimulationManager manager) {
        this.simWidth = simWidth;
        this.simHeight = simHeight;
        this.scale = scale;
        this.manager = manager;
        this.setBackground(Color.DARK_GRAY); // Tło planszy
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Wygładzanie krawędzi
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pobieramy listę osobników z managera symulacji
        List<Individual> population = manager.getPopulation();

        // Wyświetlenie wszystkich osobników na planszy
        for (int i = 0; i < population.size(); i++) {
            try {
                Individual ind = population.get(i);
                drawIndividual(g2d, ind);
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Index out of bounds: " + i);
            }
        }
    }

    private void drawIndividual(Graphics2D g, Individual ind) {
        Vector2D pos = ind.getPosition();
        double[] coords = pos.getComponents();

        // Metry -> Piksele
        int x = (int) (coords[0] * scale);
        int y = (int) (coords[1] * scale);
        int size = 8; // Wielkość kropki w pikselach

        // Dobór koloru na podstawie stanu
        HealthState state = ind.getState();
        if (state instanceof HealthySusceptible) {
            g.setColor(Color.GREEN);
        } else if (state instanceof InfectedSymptomatic) {
            g.setColor(Color.RED);
        } else if (state instanceof InfectedAsymptomatic) {
            g.setColor(Color.ORANGE);
        } else if (state instanceof Immune) {
            g.setColor(Color.BLUE);
        } else {
            g.setColor(Color.WHITE);
        }

        // Rysowanie kropki
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
}
