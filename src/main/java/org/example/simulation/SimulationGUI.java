package org.example.simulation;



import javax.swing.*;
import java.awt.*;

public class SimulationGUI extends JFrame {
    private final SimulationPanel panel;
    private final SimulationManager manager;

    public SimulationGUI(SimulationManager manager, double width, double height) {
        this.manager = manager;
        // Ustawienia okna
        setTitle("Symulacja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // wyłącza symulacje przy zamknięciu okna

        // Obliczamy rozmiar okna (10 pikseli na 1 metr)
        int scale = 10;
        int windowWidth = (int) (width * scale);
        int windowHeight = (int) (height * scale);

        this.panel = new SimulationPanel(width, height, scale, manager);
        this.panel.setPreferredSize(new Dimension(windowWidth, windowHeight));

        add(this.panel);
        pack(); // Automatyczne dopasowanie rozmiaru okna
        setLocationRelativeTo(null); // Wyśrodkowanie
        setVisible(true);
    }

    // Metoda wywoływana z pętli symulacji, żeby odświeżyć ekran
    public void refresh() {
        panel.repaint();
    }
}
