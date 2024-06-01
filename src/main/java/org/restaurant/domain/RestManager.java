package org.restaurant.domain;

import java.util.*;
import java.util.stream.Collectors;

import static org.restaurant.Main.MAX_GROUP_SIZE;

public class RestManager {
    private Map<Integer, List<Table>> tablesBySize;
    private final Queue<ClientsGroup> waitingQueue;
    private final Map<ClientsGroup, Table> seatingMap;


    public RestManager(List<Table> tables) {
        this.tablesBySize = tables.stream()
                .collect(Collectors.groupingBy(Table::getSize, Collectors.toList()));
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
        // Try to find an exact match empty table
        if (tablesBySize.containsKey(group.size)) {
            var potentialTable = tablesBySize.get(group.size)
                    .stream()
                    .filter(Table::isEmpty)
                    .findFirst();

            if (potentialTable.isPresent()) {
                assignTableSeats(group, potentialTable.get());
                return true;
            }

        }

        Table backupTable = null;
        // Try to find a larger empty table with an empty-one as a priority, if not found - a first
        // backup table, which is not empty, but can accommodate- will be returned
        for (int size = group.size + 1; size <= MAX_GROUP_SIZE; size++) {
            if (tablesBySize.containsKey(size)) {
                for (Table table : tablesBySize.get(size)) {
                    if (table.isEmpty()) {
                        assignTableSeats(group, table);
                        return true;
                    }
                    if (backupTable == null && table.canAccommodate(group.size)) {
                        backupTable = table;
                    }
                }
            }
        }
        if (backupTable != null) {
            assignTableSeats(group, backupTable);
            return true;
        }

        return false;
    }

    private void assignTableSeats(ClientsGroup group, Table table) {
        table.seatGroup(group.size);
        seatingMap.put(group, table);
    }


    private void processQueue() {
        var prevGroupSize = MAX_GROUP_SIZE + 1;
        Iterator<ClientsGroup> iterator = waitingQueue.iterator();
        while (iterator.hasNext()) {
            ClientsGroup group = iterator.next();
            if (group.size >= prevGroupSize) continue;
            if (seatGroup(group)) {
                iterator.remove();
                prevGroupSize = group.getSize();
            }
        }
    }
}
