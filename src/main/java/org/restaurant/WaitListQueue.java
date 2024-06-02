package org.restaurant;

import org.restaurant.domain.ClientsGroup;

import java.util.*;

public class WaitListQueue {

    private final Map<Integer, Queue<ClientsGroup>> waitListBySize;


    public WaitListQueue() {
        this.waitListBySize = new HashMap<>();
    }


    public void add(ClientsGroup group){
        if (waitListBySize.containsKey(group.getSize())){
            waitListBySize.get(group.getSize()).add(group);
            return;
        }
        Queue<ClientsGroup> ll = new LinkedList<>();
        ll.add(group);
        waitListBySize.put(group.getSize(), ll);
    }

    public void remove(ClientsGroup group){
        waitListBySize.get(group.getSize()).remove(group);
    }

    public ClientsGroup getMostAppropriateGroup(int freedCount){
//        TODO set the comparator by timestamp
        var pq = new PriorityQueue<ClientsGroup>();
        for (int i =0; i<=freedCount; i++){
            if (waitListBySize.containsKey(i)){
                pq.add(waitListBySize.get(i).stream().findFirst().get());
            }
        }
//        todo remove from the LL
        return pq.peek();
    }


}
