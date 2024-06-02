package org.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restaurant.domain.ClientsGroup;
import org.restaurant.domain.Table;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RestManagerTest {
    private RestManager restManager;
    private Table table2;
    private Table table4;
    private Table table6;

    @BeforeEach
    public void setUp() {
        table2 = new Table(2);
        table4 = new Table(4);
        table6 = new Table(6);
        List<Table> tables = Arrays.asList(table2, table4, table6);
        restManager = new RestManager(tables);
    }

    @Test
    public void testOnArriveAndLookupNoQueue() {
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(4);

        restManager.onArrive(group1);
        assertEquals(table2, restManager.lookup(group1));

        restManager.onArrive(group2);
        assertEquals(table4, restManager.lookup(group2));

        assertEquals(0, restManager.getQueueSize());
    }

    @Test
    public void testPreferEmptyTableInsteadOfSharing() {
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(2);
        ClientsGroup group3 = new ClientsGroup(2);

        restManager.onArrive(group1);
        restManager.onArrive(group2);
        restManager.onArrive(group3);


        assertEquals(table2, restManager.lookup(group1));
        assertEquals(table4, restManager.lookup(group2));
        assertEquals(table6, restManager.lookup(group3));
    }
    @Test
    public void testMultipleGroupsOnOneTable() {
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(6);
        ClientsGroup group3 = new ClientsGroup(2);
        ClientsGroup group4 = new ClientsGroup(2);

        restManager.onArrive(group1);
        restManager.onArrive(group2);
        restManager.onArrive(group3);
        restManager.onArrive(group4);


        assertEquals(table4, restManager.lookup(group3));
        assertEquals(table4, restManager.lookup(group4)); // Group 3 has to share table 4 with group 4
    }

    @Test
    public void testOnArriveQueue() {
        ClientsGroup group1 = new ClientsGroup(4);
        ClientsGroup group2 = new ClientsGroup(6);
        ClientsGroup group3 = new ClientsGroup(3);

        restManager.onArrive(group1);
        restManager.onArrive(group2);
        restManager.onArrive(group3);

        // Group 3 should be in queue, as it's too big for the available table
        assertNull(restManager.lookup(group3));
        assertEquals(1, restManager.getQueueSize());

        System.out.println(group2);
    }

    @Test
    public void testOnLeave() {
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(4);

        restManager.onArrive(group1);
        restManager.onArrive(group2);

        restManager.onLeave(group1);
        assertNull(restManager.lookup(group1));
        assertEquals(table4, restManager.lookup(group2));
    }

    @Test
    public void testLeaveQueueNotSeated() {
        ClientsGroup group1 = new ClientsGroup(6);
        ClientsGroup group2 = new ClientsGroup(6);

        restManager.onArrive(group1);
        restManager.onArrive(group2);

        assertEquals(table6, restManager.lookup(group1));
//        group 2 should be in the queue
        assertNull(restManager.lookup(group2));
        assertEquals(1, restManager.getQueueSize());

        restManager.onLeave(group2);
        assertEquals(0, restManager.getQueueSize());

//        should not trigger any change
        assertEquals(table6, restManager.lookup(group1));
        assertNull(restManager.lookup(group2));
    }


    @Test
    public void testProcessQueueFairness() {
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(4);
        ClientsGroup group3 = new ClientsGroup(6);
        ClientsGroup group4 = new ClientsGroup(5);
        ClientsGroup group5 = new ClientsGroup(4);
        ClientsGroup group6 = new ClientsGroup(2);

        List.of(group1, group2, group3, group4, group5, group6)
                        .forEach(group -> restManager.onArrive(group));

        assertEquals(3, restManager.getQueueSize());
        assertNull(restManager.lookup(group4)); // Group 4 should be in the queue
        assertNull(restManager.lookup(group5)); // Group 6 should be in the queue
        assertNull(restManager.lookup(group6)); // Group 6 should be in the queue

        restManager.onLeave(group1);
        assertEquals(2, restManager.getQueueSize());
        assertEquals(table2, restManager.lookup(group6)); // Group 6 should get seated, as it's only one that fits


        restManager.onLeave(group3);
        assertEquals(1, restManager.getQueueSize());
        assertEquals(table6, restManager.lookup(group4)); // Group 4 should get seated, as it's the first time-wise


        restManager.onLeave(group4);
        assertEquals(0, restManager.getQueueSize());
        assertEquals(table6, restManager.lookup(group5)); // Group 5 gets seated, using the only fitting - bigger table
    }

//    todo seat 2 small groups sync when a big group leaves the table
}
