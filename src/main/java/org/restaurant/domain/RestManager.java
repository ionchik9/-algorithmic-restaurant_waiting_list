package org.restaurant.domain;

import java.util.*;

public class RestManager {
    private final List<Table> tables;
    private final Queue<ClientsGroup> waitingQueue;
    private final Map<ClientsGroup, Table> seatingMap;


    public RestManager(List<Table> tables) {
        this.tables = new ArrayList<>(tables);
        this.waitingQueue = new LinkedList<>();
        this.seatingMap = new HashMap<>();
    }

    // new client(s) show up
    public void onArrive(ClientsGroup group) {
        // Try to seat the group
        if (!seatGroup(group)) {
            // If unable to seat, add to waiting queue
            waitingQueue.add(group);
        }
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave(ClientsGroup group) {
        // Check if the group is in the seating map
        if (seatingMap.containsKey(group)) {
            Table table = seatingMap.get(group);
            table.leaveGroup(group.getSize());
            seatingMap.remove(group);
            processQueue(); // Try to seat waiting groups
        } else {
            // If not seated, remove from the waiting queue
            waitingQueue.remove(group);
        }
    }

    // return table where a given client group is seated,
    // or null if it is still queueing or has already left
    public Table lookup(ClientsGroup group) {
        return seatingMap.get(group);
    }

    private boolean seatGroup(ClientsGroup group) {
        // First, try to find an exact match table
        for (Table table : tables) {
            if (table.getSize() == group.getSize() && table.getOccupiedSeats() == 0) {
                table.seatGroup(group.getSize());
                seatingMap.put(group, table);
                return true;
            }
        }

        // If no exact match, try to find a larger table with enough space
        for (Table table : tables) {
            if (table.canAccommodate(group.getSize()) && table.getOccupiedSeats() == 0) {
                table.seatGroup(group.getSize());
                seatingMap.put(group, table);
                return true;
            }
        }

        // If no empty table found, try to find a partially filled table with enough space
        for (Table table : tables) {
            if (table.canAccommodate(group.getSize()) && !table.isFull()) {
                table.seatGroup(group.getSize());
                seatingMap.put(group, table);
                return true;
            }
        }

        return false;
    }

    private void processQueue() {
        Iterator<ClientsGroup> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            ClientsGroup group = iterator.next();
            if (seatGroup(group)) {
                iterator.remove();
            }
        }
    }
}
