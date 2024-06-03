package org.restaurant;

import org.restaurant.domain.ClientsGroup;

import java.util.*;
import java.util.stream.IntStream;

public class WaitListQueue {

    private final Map<Integer, Queue<ClientsGroup>> waitListBySize;


    public WaitListQueue() {
        this.waitListBySize = new HashMap<>();
    }


    public int getSize() {
        return waitListBySize.values().stream().flatMapToInt(queue -> IntStream.of(queue.size())).sum();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }


    /**
     * Adds a group to the waitList.
     *
     * @param group the group to add to the waitList.
     */
//    O(1)
    public void add(ClientsGroup group) {
        waitListBySize.computeIfAbsent(group.getSize(), k -> new LinkedList<>()).add(group);
    }

    /**
     * Removes a group from the waitlist.
     *
     * @param group the group to remove from the waitlist.
     */
//    O(N), N - is the number of elements in a queue for a particular GroupSize
    public void remove(ClientsGroup group) {
        Queue<ClientsGroup> queue = waitListBySize.get(group.getSize());
        if(queue != null) {
            queue.remove(group);
        }
    }

    /**
     * Polls the most appropriate group from the waitList based on the available freed seats.
     * The method selects the group that would fit to be seated and has the earliest arrival time.
     *
     * @param freedCount the number of freed seats available.
     * @return the most appropriate group that can be seated, or null if no suitable group is found.
     */
//    O(f * log g), where f is the number of people in the group, g is the number of group sizes (f, g <= 6)
//    --> O(1)
    public ClientsGroup pollMostAppropriateGroup(int freedCount) {
//        get the first-in group of a fitting size - on top
        var pq = new PriorityQueue<Queue<ClientsGroup>>(6, Comparator.comparing(q -> q.peek().getArrivalTime()));
//        check only the queues containing the size which will fit
        for (int i = 1; i <= freedCount; i++) {
            if (waitListBySize.containsKey(i)) {
                var q = waitListBySize.get(i);
                if (q != null && !q.isEmpty()) pq.add(q);
            }
        }
//        remove the element from the queue and return
        return pq.peek() != null ? pq.peek().poll() : null;
    }


}
