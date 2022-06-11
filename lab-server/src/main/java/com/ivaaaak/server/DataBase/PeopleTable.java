package com.ivaaaak.server.DataBase;

import com.ivaaaak.common.data.Color;
import com.ivaaaak.common.data.Coordinates;
import com.ivaaaak.common.data.Country;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class PeopleTable {

    private static final int PARAMETER_KEY = 1;
    private static final int PARAMETER_NAME = 2;
    private static final int PARAMETER_COORDINATE_X = 3;
    private static final int PARAMETER_COORDINATE_Y = 4;
    private static final int PARAMETER_CREATION_DATE = 5;
    private static final int PARAMETER_HEIGHT = 6;
    private static final int PARAMETER_WEIGHT = 7;
    private static final int PARAMETER_HAIR_COLOR = 8;
    private static final int PARAMETER_NATIONALITY = 9;
    private static final int PARAMETER_LOCATION_X = 10;
    private static final int PARAMETER_LOCATION_Y = 11;
    private static final int PARAMETER_LOCATION_Z = 12;
    private static final int PARAMETER_OWNER_LOGIN = 13;
    private final ReentrantLock lock = new ReentrantLock();
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void initialize() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String creation = "CREATE TABLE IF NOT EXISTS People("
                    + "id serial primary key,"
                    + "key int not null unique,"
                    + "name varchar(70) not null ,"
                    + "coordinate_x int check (coordinate_x <= 172) not null,"
                    + "coordinate_y double precision not null,"
                    + "creation_date timestamp not null,"
                    + "height float check (height > 0),"
                    + "weight float check (height > 0),"
                    + "hair_color varchar(15) not null,"
                    + "nationality varchar(15),"
                    + "location_x bigint,"
                    + "location_y int,"
                    + "location_z int,"
                    + "owner_login varchar(70) not null)";
            statement.executeUpdate(creation);
        }
    }

    public int add(Person element) throws SQLException {
        lock.lock();
        String insert = "INSERT INTO People VALUES(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(insert)) {
            setStatementParameters(statement, element, false);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } finally {
            lock.unlock();
        }
    }

    public void setStatementParameters(PreparedStatement statement, Person person, boolean isUpdating) throws SQLException {
        statement.setInt(PARAMETER_KEY, person.getKey());
        statement.setString(PARAMETER_NAME, person.getName());
        statement.setInt(PARAMETER_COORDINATE_X, person.getCoordinates().getX());
        statement.setDouble(PARAMETER_COORDINATE_Y, person.getCoordinates().getY());
        statement.setTimestamp(PARAMETER_CREATION_DATE, Timestamp.valueOf(person.getCreationDate()));
        statement.setFloat(PARAMETER_HEIGHT, person.getHeight());
        statement.setFloat(PARAMETER_WEIGHT, person.getWeight());
        statement.setString(PARAMETER_HAIR_COLOR, person.getHairColor().toString());
        if (person.getNationality() != null) {
            statement.setString(PARAMETER_NATIONALITY, person.getNationality().toString());
        } else {
            statement.setNull(PARAMETER_NATIONALITY, Types.VARCHAR);
        }
        if (person.getLocation() != null) {
            statement.setLong(PARAMETER_LOCATION_X, person.getLocation().getX());
            statement.setInt(PARAMETER_LOCATION_Y, person.getLocation().getY());
            statement.setInt(PARAMETER_LOCATION_Z, person.getLocation().getZ());
        } else {
            statement.setNull(PARAMETER_LOCATION_X, Types.BIGINT);
            statement.setNull(PARAMETER_LOCATION_Y, Types.INTEGER);
            statement.setNull(PARAMETER_LOCATION_Z, Types.INTEGER);
        }
        if (!isUpdating) {
            statement.setString(PARAMETER_OWNER_LOGIN, person.getOwnerName());
        }
    }

    public HashSet<Integer> clear(String ownerName) throws SQLException {
        lock.lock();
        HashSet<Integer> keys = new HashSet<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("DELETE FROM People WHERE owner_login = '" + ownerName + "' RETURNING key");
            while (resultSet.next()) {
                keys.add(resultSet.getInt("key"));
            }
            return keys;
        } finally {
            lock.unlock();
        }
    }

    public int removeByID(Integer id, String ownerName) throws SQLException {
        lock.lock();
        String remove = "DELETE FROM People WHERE (id = " + id + " AND owner_login = '" + ownerName + "')";
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(remove);
        } finally {
            lock.unlock();
        }
    }

    public int update(Person oldPerson, Person newPerson, String ownerName) throws SQLException {
        lock.lock();
        String update = "UPDATE People SET "
                + "key = ?,"
                + "name = ?,"
                + "coordinate_x = ?,"
                + "coordinate_y = ?,"
                + "creation_date = ?,"
                + "height = ?,"
                + "weight = ?,"
                + "hair_color = ?,"
                + "nationality = ?,"
                + "location_x = ?,"
                + "location_y = ?,"
                + "location_z = ?"
                + "WHERE (id = " + oldPerson.getId() + " AND owner_login = '" + ownerName + "')";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            setStatementParameters(statement, newPerson, true);
            return statement.executeUpdate();
        } finally {
            lock.unlock();
        }
    }


    public ConcurrentHashMap<Integer, Person> getCollection() {
        ConcurrentHashMap<Integer, Person> people = new ConcurrentHashMap<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM People");
            while (resultSet.next()) {
                Person person = mapRowToObject(resultSet);
                people.put(person.getKey(), person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public Person mapRowToObject(ResultSet resultSet) {
        try {
            Person person = new Person(
                    resultSet.getString("name"),
                    new Coordinates(
                            resultSet.getInt("coordinate_x"),
                            resultSet.getDouble("coordinate_y")
                    ),
                    resultSet.getTimestamp("creation_date").toLocalDateTime(),
                    resultSet.getFloat("height"),
                    resultSet.getFloat("weight"),
                    Color.valueOf(resultSet.getString("hair_color")),
                    resultSet.getString("nationality") != null ? Country.valueOf(resultSet.getString("nationality")) : null,
                    new Location(
                            resultSet.getLong("location_x"),
                            resultSet.getInt("location_y"),
                            resultSet.getInt("location_x")
                    ),
                    resultSet.getString("owner_login")
            );
            person.setId(resultSet.getInt("id"));
            person.setKey(resultSet.getInt("key"));
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
