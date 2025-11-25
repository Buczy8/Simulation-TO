package org.example.memento;

public class Caretaker {
    // przechowujemy ostatnie Memento
    private Memento savedMemento;

    public void saveMemento(Memento m) {
        this.savedMemento = m;
        System.out.println("Zapisano stan symulacji.");
    }


    // Zwraca ostatnie zapisane Memento
    public Memento getMemento() {
        if (savedMemento != null) {
            System.out.println("Wczytano stan symulacji.");
            return savedMemento;
        }
        System.out.println("Brak zapisanych stanów.");
        return null;
    }
}
