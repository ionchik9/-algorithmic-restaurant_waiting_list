package org.restaurant;

import org.restaurant.domain.ClientsGroup;
import org.restaurant.domain.Table;

import java.util.*;
import java.util.stream.Collectors;

import static org.restaurant.Main.MAX_GROUP_SIZE;

public class RestManager {
    private final Map<Integer, List<Table>> tablesBySize;
    private final Queue<ClientsGroup> waitingQueue;
    private final Map<ClientsGroup, Table> seatingMap;


    public RestManager(List<Table> tables) {
        this.tablesBySize = tables.stream()
                .collect(Collectors.groupingBy(Table::getSize, Collectors.toList()));
        this.waitingQueue = new LinkedList<>();
        this.seatingMap = new HashMap<>();
    }

    public int getQueueSize() {
        return waitingQueue.size();
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

    // return table where a given client group is seated,
    // or null if it is still queueing or has already left
    public Table lookup(ClientsGroup group) {
        return seatingMap.get(group);
    }

    /**
     * Attempts to seat a client group.
     * Checks an empty table of the exact size, if not found, searches for an empty bigger table,
     * if doesn't work - finds a partially occupied table with enough empty seats
     * @param group the client group
     * @return true if the group was seated, false otherwise
     */
//    Time complexity: O(m*k), where m is the count of different table sizes (m<=6) ->
//    can be simplified to O(k), where k is the number of tables of a certain size
    private boolean seatGroup(ClientsGroup group) {
        // Try to find an exact match empty table
        if (tablesBySize.containsKey(group.getSize())) {
            var potentialTable = tablesBySize.get(group.getSize())
                    .stream()
                    .filter(Table::isEmpty)
                    .findFirst();

            if (potentialTable.isPresent()) {
                assignTableSeats(group, potentialTable.get());
                return true;
            }

        }

        // Try to find a larger empty table, if not found - a first
        // backup table, which is not empty, but can accommodate, will be returned
        Table backupTable = null;
        for (int size = group.getSize() + 1; size <= MAX_GROUP_SIZE; size++) {
            if (tablesBySize.containsKey(size)) {
                for (Table table : tablesBySize.get(size)) {
                    if (table.isEmpty()) {
                        assignTableSeats(group, table);
                        return true;
                    }
                    if (backupTable == null && table.canAccommodate(group.getSize())) {
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


    /**
     * Assigns a group to a table and updates the seating map.
     *
     * @param group the client group
     * @param table the table to seat the group at
     */
    private void assignTableSeats(ClientsGroup group, Table table) {
        table.seatGroup(group.getSize());
        seatingMap.put(group, table);
    }

    /**
     * Processes the queue to seat waiting groups based on freed seats.
     *
     * @param freedCount the number of freed seats
     *
     */
//    O(N) time complexity, where N is the number of groups in the wait-list,
//    A potential optimization could involve using a Map of {fittingSize: FifoQueue},
//    which would result in a constant lookup time.
//    In that case, removing from the queue would require finding the group in all the queues,
//    where it is present in order to perform a consistent removal, which again might be sorted out
//    with a lookup Map, but it requires more consultation whether using that much space is desirable.

    private void processQueue(int freedCount, Table table) {
        int availableSeats = freedCount;
        var iterator = waitingQueue.iterator();
        while (iterator.hasNext() && availableSeats > 0) {
            ClientsGroup group = iterator.next();
            if (group.getSize() <= availableSeats){
                assignTableSeats(group, table);
                iterator.remove();
                availableSeats -= group.getSize();
            }
        }

    }

}
