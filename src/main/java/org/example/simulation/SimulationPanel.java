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
    private final int scale;
    private final SimulationManager manager;

    public SimulationPanel(int scale, SimulationManager manager) {
        this.scale = scale;
        this.manager = manager;
        this.setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // wygładzanie krawędzi kółek
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pobieramy kopię listy z managera
        List<Individual> population = manager.getPopulationForGUI();

        if (population == null) return;

        // Rysowanie osobników
        for (Individual ind : population) {
            drawIndividual(g2d, ind);
        }

        // Rysowanie instrukcji w rogu do zapisania i wczytania stanu
        g2d.setColor(Color.WHITE);
        g2d.drawString("S - Zapisz | L - Wczytaj", 10, 20);
    }

    // Rysowanie osobnika
    private void drawIndividual(Graphics2D g, Individual ind) {
        Vector2D pos = ind.getPosition();

        // Konwersja metrów na piksele
        int x = (int) (pos.getComponents()[0] * scale);
        int y = (int) (pos.getComponents()[1] * scale);
        int size = 8; // Rozmiar kropki


        HealthState state = ind.getState();

        switch (state) {
            case HealthySusceptible healthySusceptible -> g.setColor(Color.GREEN); // Zdrowy
            case InfectedSymptomatic infectedSymptomatic -> g.setColor(Color.RED); // Chory objawowy
            case InfectedAsymptomatic infectedAsymptomatic -> g.setColor(Color.YELLOW); // Chory bezobjawowy
            case Immune immune -> g.setColor(Color.BLUE); // Odporny
            case null, default -> g.setColor(Color.WHITE); // Błąd/Inny
        }

        // Rysowanie kropki
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
}