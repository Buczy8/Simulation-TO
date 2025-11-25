package org.example.simulation;



import javax.swing.*;
import java.awt.*;

public class SimulationGUI extends JFrame {
    private final SimulationPanel panel;

    public SimulationGUI(SimulationManager manager, double width, double height) {
        // Ustawienia okna
        setTitle("Symulacja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // wyłącza symulacje przy zamknięciu okna

        // Obliczamy rozmiar okna (10 pikseli na 1 metr)
        int scale = 10;
        int windowWidth = (int) (width * scale);
        int windowHeight = (int) (height * scale);

        this.panel = new SimulationPanel(scale, manager);
        this.panel.setPreferredSize(new Dimension(windowWidth, windowHeight));

        add(this.panel);
        pack(); // Automatyczne dopasowanie rozmiaru okna
        setLocationRelativeTo(null); // Wyśrodkowanie
        setVisible(true);

        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
                    manager.saveSimulationState();
                }
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {
                    manager.loadSimulationState();
                }
            }
        });
        this.setFocusable(true); // Ważne, żeby okno przechwytywało klawisze
    }

    // Metoda wywoływana z pętli symulacji, żeby odświeżyć ekran
    public void refresh() {
        panel.repaint();
    }
}
