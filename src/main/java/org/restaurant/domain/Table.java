package org.restaurant.domain;

import java.util.Objects;
import java.util.UUID;

public class Table {
    private final UUID id;
    private final int size; // number of chairs
    private int occupiedSeats; // number of occupied seats

    public Table(int size) {
        this.id = UUID.randomUUID();
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

    public boolean isEmpty(){
        return occupiedSeats == 0;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Table(id=%s, size=%d)", id, size);
    }
}
