package com.ivaaaak.server.DataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseManager {

    private final PeopleTable peopleTable = new PeopleTable();
    private final UsersTable usersTable = new UsersTable();

    public void setConnection(Connection connection) {
        peopleTable.setConnection(connection);
        usersTable.setConnection(connection);
        initializeTables();
    }

    public void initializeTables() {
        try {
            peopleTable.initialize();
            usersTable.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PeopleTable getPeopleTable() {
        return peopleTable;
    }

    public UsersTable getUsersTable() {
        return usersTable;
    }
}
