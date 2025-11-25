package org.example.simulation;



import org.example.simulation.interfaces.SimulationListener;

import javax.swing.*;
import java.awt.*;

public class SimulationGUI extends JFrame implements SimulationListener {
    private final SimulationPanel panel;

    public SimulationGUI(SimulationManager manager, double width, double height) {
        // Ustawienia okna
        setTitle("Symulacja Wirusa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Obliczamy rozmiar okna (10 pikseli na 1 metr)
        int scale = 10;
        int windowWidth = (int) (width * scale);
        int windowHeight = (int) (height * scale);

        // Inicjalizacja panelu rysującego
        this.panel = new SimulationPanel(scale, manager);
        this.panel.setPreferredSize(new Dimension(windowWidth, windowHeight));

        add(this.panel);
        pack(); // Automatyczne dopasowanie rozmiaru okna do panelu
        setLocationRelativeTo(null); // Wyśrodkowanie na ekranie
        setVisible(true);

        // Obsługa klawiszy
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
                    manager.saveState();
                    System.out.println("Zapisano stan (S).");
                }
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {
                    manager.loadState();
                    System.out.println("Wczytano stan (L).");
                }
            }
        });
        this.setFocusable(true); // żeby okno przechwytywało klawisze
    }

    // Metoda wywoływana z pętli symulacji, żeby odświeżyć ekran
    public void refresh() {
        panel.repaint();
    }

    // rysownie każdego kroku
    @Override
    public void onSimulationStep() {
        SwingUtilities.invokeLater(this::repaint);
    }

    // Wyświetlanie statystyk na konsoli
    @Override
    public void onStatsUpdated(long step, int size, long h, long i, long im) {
        System.out.println("Krok: " + step + " | Zakażeni: " + i);
    }
}
