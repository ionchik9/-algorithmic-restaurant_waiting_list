[thread safety should be ignored]
Your restaurant has a set of tables of different sizes: each table can accommodate 2, 3, 4, 5 or 6 persons. 
Clients arrive alone or in groups, up to 6 persons. Clients within a given group must be seated together at one table, hence you can direct a group only to a table, 
which can accommodate them all. If there is no table with the required number of empty chairs, the group has to wait in the queue.

Once seated, the group cannot change the table, i.e. you cannot move a group from one table to another to make room for new clients.

Client groups must be served in the order of arrival with one exception: if there is enough room at a table for a smaller group arriving later, 
you can seat them before the larger group(s) in the queue. For example, if there is a six-person group waiting for a six-seat table and
there is a two-person group queuing  or arriving you can send them directly to a table with two empty chairs.

Groups may share tables.

Of course the system assumes that any bigger group may get bored of seeing smaller groups arrive and get their tables ahead of them, 
and then decide to leave, which would mean that they abandon the queue without being served.
