package org.restaurant.domain;

import java.util.Objects;
import java.util.UUID;

public class ClientsGroup {
    private final UUID id;
    public final int size; // number of clients

    public ClientsGroup(int size) {
        this.id = UUID.randomUUID();
        this.size = size;
    }

    public int getSize() {
        return size;
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
