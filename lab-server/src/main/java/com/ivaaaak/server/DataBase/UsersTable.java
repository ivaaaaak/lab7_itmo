package com.ivaaaak.server.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class UsersTable {

    private static final int PARAMETER_LOGIN = 1;
    private static final int PARAMETER_PASSWORD = 2;
    private final ReentrantLock lock = new ReentrantLock();
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String creation = "CREATE TABLE IF NOT EXISTS Users("
                    + "id serial primary key,"
                    + "login varchar(70) not null unique,"
                    + "password varchar(70) not null)";
            statement.executeUpdate(creation);
        }
    }


    public int add(String login, String password) throws SQLException {
        lock.lock();
        String insert = "INSERT INTO Users VALUES(default, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insert)) {
            setStatementParameters(statement, login, password);
            statement.executeUpdate();
            return 1;
        } finally {
            lock.unlock();
        }
    }


    public void setStatementParameters(PreparedStatement statement, String login, String password) {
        try {
            statement.setString(PARAMETER_LOGIN, login);
            statement.setString(PARAMETER_PASSWORD, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<String, String> getCollection() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");
            while (resultSet.next()) {
                map.put(resultSet.getString("login"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

}
