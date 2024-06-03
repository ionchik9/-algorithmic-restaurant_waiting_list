package org.restaurant;

import org.restaurant.domain.ClientsGroup;
import org.restaurant.domain.Table;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.restaurant.Main.MAX_GROUP_SIZE;

public class RestManager {
    private final Map<Integer, List<Table>> tablesBySize;
    private final WaitListQueue waitingQueue;
    private final Map<ClientsGroup, Table> seatingMap;


    public RestManager(List<Table> tables) {
        this.tablesBySize = tables.stream()
                .collect(Collectors.groupingBy(Table::getSize, Collectors.toList()));
        this.waitingQueue = new WaitListQueue();
        this.seatingMap = new HashMap<>();
    }

    public int getQueueSize() {
        return waitingQueue.getSize();
    }

    // new client(s) show up
    public void onArrive(ClientsGroup group) {
        // Try to seat the group
        if (!seatGroup(group)) {
            // If unable to seat, add to waiting queue
            group.setArrivalTime(Instant.now());
            waitingQueue.add(group);
        }
    }

    // client(s) leave, either served or simply abandoning the queue
    public void onLeave(ClientsGroup group) {
        // Check if the group is in the seating map, to accommodate next in the Queue
        if (seatingMap.containsKey(group)) {
            Table table = seatingMap.get(group);
            table.leaveGroup(group.getSize());
            seatingMap.remove(group);
            processQueue(group.getSize(), table); // Try to seat waiting groups
        } else {
            // If not seated, remove from the waiting queue
            waitingQueue.remove(group);
        }
    }

    /**
     * @return Table where a given client group is seated,
     * or null if it is still queueing or has already left
     */
    public Table lookup(ClientsGroup group) {
        return seatingMap.get(group);
    }

    /**
     * Attempts to seat a client group.
     * Checks an empty table of the exact size, if not found, searches for an empty bigger table,
     * if it doesn't work - finds a partially occupied table with enough empty seats
     *
     * @param group the client group
     * @return true if the group was seated, false otherwise
     */
//    Time complexity: O(m*k), where m is the count of different table sizes (m<=6) -->
//     O(k), where k is the number of tables of a certain size
    private boolean seatGroup(ClientsGroup group) {
        // Try to find an exact match empty table
        if (tablesBySize.containsKey(group.getSize())) {
            var matchingSizeEmptyTable = tablesBySize.get(group.getSize())
                    .stream()
                    .filter(Table::isEmpty)
                    .findFirst();

            if (matchingSizeEmptyTable.isPresent()) {
                return assignTableSeats(group, matchingSizeEmptyTable.get());
            }

        }

        // Try to find a larger empty table, if not found - a first
        // partially occupied table, which is not empty, but can accommodate, will be assigned
        Table partiallyOccupiedTable = null;
        for (int size = group.getSize() + 1; size <= MAX_GROUP_SIZE; size++) {
            if (tablesBySize.containsKey(size)) {
                for (Table table : tablesBySize.get(size)) {
                    if (table.isEmpty()) {
                        return assignTableSeats(group, table);
                    }
                    if (partiallyOccupiedTable == null && table.canAccommodate(group.getSize())) {
                        partiallyOccupiedTable = table;
                    }
                }
            }
        }
        if (partiallyOccupiedTable != null) {
            return assignTableSeats(group, partiallyOccupiedTable);
        }

        return false;
    }


    /**
     * Assigns a group to a table and updates the seating map.
     *
     * @param group the client group
     * @param table the table to seat the group at
     * @return true
     */
    private boolean assignTableSeats(ClientsGroup group, Table table) {
        table.seatGroup(group.getSize());
         seatingMap.put(group, table);
         return true;
    }

    /**
     * Processes the queue to assign seats for waiting groups based on freed seats.
     *
     * @param freedCount the number of freed seats
     */
    private void processQueue(int freedCount, Table table) {
        int availableSeats = freedCount;
        while (availableSeats > 0) {
            ClientsGroup groupToBeSeated = waitingQueue.pollMostAppropriateGroup(availableSeats);
//           if there's no appropriate size group in the waitList break (all the groups in the waitList are bigger than the available number of seats).
            if(groupToBeSeated == null) break;
            assignTableSeats(groupToBeSeated, table);
            availableSeats -= groupToBeSeated.getSize();
        }
    }

}
