package com.ivaaaak.common.util;

import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public interface PeopleCollectionStorable {

    LocalDate getCreationDate();
    ConcurrentHashMap<Integer, Person> getPeopleCollection();

    void addPerson(Integer key, Person person, String ownerName) throws SQLException;
    boolean removePerson(Integer key, String ownerName) throws SQLException;
    boolean replacePerson(Integer oldKey, Person newPerson, String ownerName) throws SQLException;
    void clear(String ownerName) throws SQLException;

    Person[] getAllPeople();
    Person[] getMatchingPeople(Location location);
    Person[] getMatchingPeople(String substring);
    Person getMaxColorPerson();
    Integer getMatchingIDKey(Integer id);

    void removeLowerPeople(Person person, String ownerName) throws SQLException;
    CommandResult replaceIfNewGreater(Integer oldKey, Person newPerson, String ownerName) throws SQLException;
    CommandResult replaceIfNewLower(Integer oldKey, Person newPerson, String ownerName) throws SQLException;

}
