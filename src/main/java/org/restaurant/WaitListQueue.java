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


    public void add(ClientsGroup group) {
        if (waitListBySize.containsKey(group.getSize())) {
            waitListBySize.get(group.getSize()).add(group);
            return;
        }
        Queue<ClientsGroup> ll = new LinkedList<>();
        ll.add(group);
        waitListBySize.put(group.getSize(), ll);
    }

    public void remove(ClientsGroup group) {
        waitListBySize.get(group.getSize()).remove(group);
    }

    public ClientsGroup pollMostAppropriateGroup(int freedCount) {
        var pq = new PriorityQueue<Queue<ClientsGroup>>(6, Comparator.comparing(q -> q.peek().getArrivalTime()));
        for (int i = 1; i <= freedCount; i++) {
            if (waitListBySize.containsKey(i)) {
                var q = waitListBySize.get(i);
                if (q != null && !q.isEmpty()) pq.add(waitListBySize.get(i));
            }
        }
        return pq.peek() != null ? pq.peek().poll() : null;
    }


}
