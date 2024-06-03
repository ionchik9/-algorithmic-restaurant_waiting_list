package org.restaurant.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ClientsGroup {
    private final UUID id;
    private final int size; // number of clients
    private Instant arrivalTime;

    public ClientsGroup(int size) {
        this.id = UUID.randomUUID();
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientsGroup that = (ClientsGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ClientsGroup(id=%s, size=%d)", id, size);
    }
}
