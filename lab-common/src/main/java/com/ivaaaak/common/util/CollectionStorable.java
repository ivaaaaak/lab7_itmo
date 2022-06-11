package com.ivaaaak.common.util;

import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public interface CollectionStorable {

    LocalDate getCreationDate();
    ConcurrentHashMap<Integer, Person> getPeopleCollection();

    boolean addPerson(Integer key, Person person);
    boolean removePerson(Integer key, String ownerName);
    boolean replacePerson(Integer oldKey, Person newPerson, String ownerName);
    boolean clear(String ownerName);

    Person[] getAllPeople();
    Person[] getMatchingPeople(Location location);
    Person[] getMatchingPeople(String substring);
    Person getMaxColorPerson();
    Integer getMatchingIDKey(Integer id);

    boolean removeLowerPeople(Person person, String ownerName);
    CommandResult replaceIfNewGreater(Integer oldKey, Person newPerson, String ownerName);
    CommandResult replaceIfNewLower(Integer oldKey, Person newPerson, String ownerName);

    CommandResult authorizeUser(String login, String password);
    CommandResult registerUser(String login, String password);
}
