package org.restaurant.domain;

public class Table {
    private final int size; // number of chairs
    private int occupiedSeats; // number of occupied seats

    public Table(int size) {
        this.size = size;
        this.occupiedSeats = 0;
    }

    public int getSize() {
        return size;
    }

    public int getOccupiedSeats() {
        return occupiedSeats;
    }

    public boolean isFull() {
        return occupiedSeats == size;
    }

    public boolean canAccommodate(int groupSize) {
        return size - occupiedSeats >= groupSize;
    }

    public void seatGroup(int groupSize) {
        occupiedSeats += groupSize;
    }

    public void leaveGroup(int groupSize) {
        occupiedSeats -= groupSize;
    }

}
