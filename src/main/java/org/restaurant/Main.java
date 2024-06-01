package org.restaurant;

import org.restaurant.domain.ClientsGroup;
import org.restaurant.domain.RestManager;
import org.restaurant.domain.Table;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static final int MAX_GROUP_SIZE = 6;

    public static void main(String[] args) {
        List<Table> tables = Arrays.asList(new Table(2), new Table(3), new Table(4), new Table(5), new Table(6));
        RestManager manager = new RestManager(tables);

        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(4);
        ClientsGroup group3 = new ClientsGroup(3);

        manager.onArrive(group1);
        manager.onArrive(group2);
        manager.onArrive(group3);

        System.out.println(manager.lookup(group1).getSize()); // Should print 2
        System.out.println(manager.lookup(group2).getSize()); // Should print 4
        System.out.println(manager.lookup(group3).getSize()); // Should print 3

        manager.onLeave(group2);

        ClientsGroup group4 = new ClientsGroup(4);
        manager.onArrive(group4);

        System.out.println(manager.lookup(group4).getSize()); // Should print 4
    }
}