package org.example.individuals;

import org.example.state.HealthySusceptible;
import org.example.state.Immune;
import org.example.state.InfectedSymptomatic;
import org.example.state.interfaces.HealthState;
import org.example.state.InfectedAsymptomatic;
import org.example.vectors.Vector2D;

import java.util.Random;

public class Individual {

    // klasa wektora z Lab 2
    private Vector2D position;
    private Vector2D velocity;

    // Aktualny stan zdrowia
    private HealthState state;

    private boolean isRemoved = false;

    // Licznik kontaktu
    private int contactTimer = 0;

    public Individual(double x, double y, boolean isImmune) {
        this.position = new Vector2D(x, y);
        // Losowa prędkość i kierunek, max. 2.5 m/s
        this.velocity = Vector2D.randomVelocity(2.5);

        // Domyślny stan
        if (isImmune) {
            // Jeśli true -> ustawiamy stan na Odporny
            this.state = new Immune();
        } else {
            // Jeśli false -> ustawiamy stan na Zdrowy
            this.state = new HealthySusceptible();
        }
    }

    // Metoda wywoływana 25 razy na sekundę
    public void tick(double width, double height) {
        // Losowa zmiana prędkości przed ruchem
        updateVelocity();

        //  Ruch z nową prędkością
        position = position.add(velocity);

        // Nowy stan
        state.update(this);

        // Sprawdzenie, czy osobnik nie wyszedł poza obszar
        checkBoundaries(width, height);
    }

    // losowanie zmian prędkości
    private void updateVelocity() {
        Random rand = new Random();

        // Siła losowych zmian
        double changeFactor = 0.1;

        // Tworzymy losowy wektor zmiany
        double dx = (rand.nextDouble() - 0.5) * changeFactor;
        double dy = (rand.nextDouble() - 0.5) * changeFactor;
        Vector2D perturbation = new Vector2D(dx, dy);

        // Dodajemy zmianę do aktualnej prędkości
        Vector2D newVelocity = velocity.add(perturbation);

        // Sprawdzamy, czy nie przekroczono limitu 2.5 m/s
        double speed = newVelocity.abs();
        double maxSpeed = 2.5;

        if (speed > maxSpeed) {
            // Skalujemy wektor w dół do 2.5, zachowując kierunek
            double scale = maxSpeed / speed;
            double[] comps = newVelocity.getComponents();

            // Mnożenie przez skalar
            newVelocity = new Vector2D(comps[0] * scale, comps[1] * scale);
        }
        // Przypisanie nowej prędkości
        this.velocity = newVelocity;
    }

    // Zmiana stanu
    public void setState(HealthState newState) {
        this.state = newState;
        System.out.println("Zmiana stanu na: " + newState.getName());
    }

    // Inkrementacja licznika kontaktu
    public void incrementContactTime() {
        this.contactTimer++;
    }

    // Resetujemy licznik (gdy oddali się od zakażonego)
    public void resetContactTime() {
        this.contactTimer = 0;
    }

    // Sprawdzamy, czy minęły 3 sekundy (75 kroków)
    public boolean hasContactLastedLongEnough() {
        return this.contactTimer >= (3 * 25); // 75 kroków
    }

    // Metoda wywoływana, gdy dochodzi do zakażenia
    public void tryInfect() {
        // Jeśli jest już odporny lub chory, nic nie robimy
        if (!(state instanceof HealthySusceptible)) return;

        Random rand = new Random();
        // Losujemy rodzaj zakażenia
        if (rand.nextBoolean()) {
            setState(new InfectedSymptomatic());
        } else {
            setState(new InfectedAsymptomatic());
        }
    }

    // Zwraca pozycje osobnika
    public Vector2D getPosition() {
        return position;
    }

    // Zwraca stan zdrowia osobnika
    public HealthState getState() {
        return state;
    }

    // Zwraca, czy osobnik został usunięty
    public boolean isRemoved() {
        return isRemoved;
    }

    // Sprawdzenie, czy osobnik wyszedł poza obszar
    public void checkBoundaries(double width, double height) {
        // Pobieramy aktualne współrzędne
        double[] pos = position.getComponents();
        double x = pos[0];
        double y = pos[1];

        // Pobieramy aktualną prędkość
        double[] vel = velocity.getComponents();
        double vx = vel[0];
        double vy = vel[1];

        boolean hitX = false;
        boolean hitY = false;

        // Sprawdzenie, czy wyszedł poza X
        if (x <= 0 || x >= width) {
            hitX = true;
        }

        // Sprawdzenie, czy wyszedł poza Y
        if (y <= 0 || y >= height) {
            hitY = true;
        }

        // Jeśli dotknął którejkolwiek granicy
        if (hitX || hitY) {
            Random rand = new Random();

            // Prawdopodobieństwo 50% na opuszczenie obszaru
            if (rand.nextBoolean()) {
                this.isRemoved = true; // Oznaczamy do usunięcia
            } else {
                // Jeśli uderzył w ścianę pionową, odwracamy składową VX
                if (hitX) {
                    vx = -vx;
                    // Korekta pozycji, żeby nie utknął w ścianie
                    if (x <= 0) x = 0.1;
                    if (x >= width) x = width - 0.1;
                }

                // Jeśli uderzył w ścianę poziomą, odwracamy składową VY
                if (hitY) {
                    vy = -vy;
                    // Korekta pozycji
                    if (y <= 0) y = 0.1;
                    if (y >= height) y = height - 0.1;
                }

                // Aktualizujemy wektor
                this.velocity = new Vector2D(vx, vy);
                this.position = new Vector2D(x, y);
            }
        }
    }
}

