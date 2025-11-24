package org.example.individuals;

import org.example.state.HealthySusceptible;
import org.example.state.Immune;
import org.example.state.InfectedSymptomatic;
import org.example.state.interfaces.HealthState;
import org.example.state.InfectedAsymptomatic;
import org.example.vectors.Vector2D;

import java.util.Random;

public class Individual {

    // Twoja klasa wektora z Lab 2
    private Vector2D position;
    private Vector2D velocity;

    // Aktualny stan zdrowia (Wzorzec State)
    private HealthState state;

    private boolean isRemoved = false;

    // Licznik kontaktu (potrzebny do warunku 3 sekund bliskiego kontaktu)
    private int contactTimer = 0;

    public Individual(double x, double y, boolean isImmune) {
        this.position = new Vector2D(x, y);
        // Losowa prędkość i kierunek, max 2.5 [cite: 4]
        this.velocity = Vector2D.randomVelocity(2.5);

        // Domyślny stan (można zmienić w konstruktorze zależnie od parametrów symulacji)
        if (isImmune) {
            // Jeśli true -> ustawiamy stan na Odporny
            // Taki osobnik nigdy nie wywoła metody tryInfect() skutecznie
            this.state = new Immune();
        } else {
            // Jeśli false -> ustawiamy stan na Zdrowy (Podatny) [cite: 13]
            this.state = new HealthySusceptible();
        }
    }

    // Metoda wywoływana 25 razy na sekundę
    public void tick(double width, double height) {
        // 0. Losowa zmiana prędkości przed ruchem
        updateVelocity();

        // 1. Ruch (z nową, ewentualnie zmienioną prędkością)
        position = position.add(velocity);

        // 2. Aktualizacja logiki stanu
        state.update(this);

        // 3. Logika granic
        checkBoundaries(width, height);
    }

    // Nowa metoda prywatna do losowania zmian prędkości
    private void updateVelocity() {
        Random rand = new Random();

        // Siła losowych zmian (im mniejsza liczba, tym płynniejszy ruch)
        double changeFactor = 0.2;

        // Tworzymy mały losowy wektor zmiany (-0.1 do 0.1 przy changeFactor=0.2)
        double dx = (rand.nextDouble() - 0.5) * changeFactor;
        double dy = (rand.nextDouble() - 0.5) * changeFactor;
        Vector2D perturbation = new Vector2D(dx, dy);

        // Dodajemy zmianę do aktualnej prędkości
        Vector2D newVelocity = velocity.add(perturbation);

        // Sprawdzamy, czy nie przekroczono limitu 2.5 m/s [cite: 4, 5]
        double speed = newVelocity.abs();
        double maxSpeed = 2.5;

        if (speed > maxSpeed) {
            // Skalujemy wektor w dół do 2.5, zachowując kierunek
            double scale = maxSpeed / speed;
            double[] comps = newVelocity.getComponents();

            // Ręczne mnożenie przez skalar (chyba że masz metodę .multiply() w Vector2D)
            newVelocity = new Vector2D(comps[0] * scale, comps[1] * scale);
        }

        this.velocity = newVelocity;
    }

    // Zmiana stanu (kluczowe dla wzorca State)
    public void setState(HealthState newState) {
        this.state = newState;
        System.out.println("Zmiana stanu na: " + newState.getName());
    }

    public void incrementContactTime() {
        this.contactTimer++;
    }

    // Resetujemy licznik (gdy oddali się od zakażonego)
    public void resetContactTime() {
        this.contactTimer = 0;
    }

    // Sprawdzamy, czy minęły 3 sekundy (3s * 25 kroków = 75)
    public boolean hasContactLastedLongEnough() {
        return this.contactTimer >= (3 * 25); // 75 kroków
    }

    // Metoda wywoływana, gdy dochodzi do zakażenia
    public void tryInfect() {
        // Jeśli jest już odporny lub chory, nic nie robimy
        if (!(state instanceof HealthySusceptible)) return;

        Random rand = new Random();
        // Losujemy rodzaj zakażenia (alternatywa wykluczająca)
        if (rand.nextBoolean()) {
            setState(new InfectedSymptomatic());
        } else {
            setState(new InfectedAsymptomatic());
        }
    }

    // Gettery, Settery i logika granic...
    public Vector2D getPosition() {
        return position;
    }

    public HealthState getState() {
        return state;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

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

        // Sprawdzenie czy wyszedł poza X (lewo lub prawo)
        if (x <= 0 || x >= width) {
            hitX = true;
        }

        // Sprawdzenie czy wyszedł poza Y (góra lub dół)
        if (y <= 0 || y >= height) {
            hitY = true;
        }

        // Jeśli dotknął którejkolwiek granicy
        if (hitX || hitY) {
            Random rand = new Random();

            // Prawdopodobieństwo 50% na opuszczenie obszaru [cite: 7]
            if (rand.nextBoolean()) {
                this.isRemoved = true; // Oznaczamy do usunięcia
            } else {
                // Prawdopodobieństwo 50% na zawrócenie (odbicie) [cite: 7]

                // Jeśli uderzył w ścianę pionową (X), odwracamy składową VX
                if (hitX) {
                    vx = -vx;
                    // Korekta pozycji, żeby nie "utknął" w ścianie
                    if (x <= 0) x = 0.1;
                    if (x >= width) x = width - 0.1;
                }

                // Jeśli uderzył w ścianę poziomą (Y), odwracamy składową VY
                if (hitY) {
                    vy = -vy;
                    // Korekta pozycji
                    if (y <= 0) y = 0.1;
                    if (y >= height) y = height - 0.1;
                }

                // Aktualizujemy wektory (tworzymy nowe, bo Vector2D jest niemutowalny/immutable w dobrym stylu)
                this.velocity = new Vector2D(vx, vy);
                this.position = new Vector2D(x, y);
            }
        }
    }
}

