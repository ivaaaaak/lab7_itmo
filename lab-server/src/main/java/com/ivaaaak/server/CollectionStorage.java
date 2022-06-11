package com.ivaaaak.server;

import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.server.DataBase.DataBaseManager;
import com.ivaaaak.server.DataBase.PeopleTable;
import com.ivaaaak.server.DataBase.UsersTable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class CollectionStorage implements com.ivaaaak.common.util.CollectionStorable {

    private final PeopleTable peopleTable;
    private final UsersTable usersTable;
    private final ConcurrentHashMap<Integer, Person> peopleCollection;
    private final ConcurrentHashMap<String, String> usersCollection;
    private final LocalDate creationDate;

    public CollectionStorage(DataBaseManager dataBaseManager) {
        peopleTable = dataBaseManager.getPeopleTable();
        usersTable = dataBaseManager.getUsersTable();
        creationDate = LocalDate.now();
        peopleCollection = peopleTable.getCollection();
        usersCollection = usersTable.getCollection();
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public ConcurrentHashMap<Integer, Person> getPeopleCollection() {
        return peopleCollection;
    }

    public boolean checkForLogin(String login) {
        return usersCollection.containsKey(login);
    }

    public boolean checkPassword(String login, String password) {
        String realPassword = usersCollection.get(login);
        return realPassword.equals(password);
    }

    public boolean addPerson(Integer key, Person person) {
        try {
            person.setKey(key);
            int id = peopleTable.add(person);
            person.setId(id);
            peopleCollection.put(key, person);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(String login, String password) throws SQLException {
        usersTable.add(login, password);
        usersCollection.put(login, password);
    }

    public boolean replacePerson(Integer oldKey, Person newPerson, String ownerName) {
        Person oldPerson = peopleCollection.get(oldKey);
        newPerson.setKey(oldKey);
        try {
            if (peopleTable.update(oldPerson, newPerson, ownerName) != 0) {
                newPerson.setId(oldPerson.getId());
                peopleCollection.replace(oldKey, oldPerson, newPerson);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clear(String ownerName) {
        try {
            HashSet<Integer> keys = peopleTable.clear(ownerName);
            for (Integer key: keys) {
                peopleCollection.remove(key);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removePerson(Integer key, String ownerName) {
        try {
            int id = peopleCollection.get(key).getId();
            if (peopleTable.removeByID(id, ownerName) != 0) {
                peopleCollection.remove(key);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Person[] getAllPeople() {
        return peopleCollection.values().toArray(new Person[0]);
    }

    public Person[] getMatchingPeople(Location location) {
        return peopleCollection.values().stream()
                .filter(x -> (x.getLocation() != null))
                .filter(x -> (x.getLocation().equals(location)))
                .toArray(Person[]::new);
    }

    public Person[] getMatchingPeople(String substring) {
        return peopleCollection.values().stream()
                .filter(x -> x.getName().startsWith(substring))
                .toArray(Person[]::new);
    }

    public Person getMaxColorPerson() {
        Comparator<Person> comparator = Comparator.comparing(Person::getHairColor);
        Optional<Person> maxPerson = peopleCollection.values().stream().max(comparator);
        return maxPerson.orElse(null);
    }

    public boolean removeLowerPeople(Person person, String ownerName) {
       Person[] lowerPeople = peopleCollection.values().stream()
               .filter(x -> x.compareTo(person) < 0)
               .toArray(Person[]::new);
       try {
           for (Person p : lowerPeople) {
               if (peopleTable.removeByID(p.getId(), ownerName) != 0) {
                   peopleCollection.remove(p.getKey());
               }
           }
           return true;
       } catch (SQLException e) {
           e.printStackTrace();
           return false;
       }
    }

    public CommandResult replaceIfNewGreater(Integer oldKey, Person newPerson, String ownerName) {
        Person oldPerson = peopleCollection.get(oldKey);
        if (oldPerson.compareTo(newPerson) < 0) {
            if (replacePerson(oldKey, newPerson, ownerName)) {
                return new CommandResult("The element has been replaced");
            }
            return new CommandResult("You don't have access to this element");
        }
        return new CommandResult("The element is greater than a new one or equal");
    }

    public CommandResult replaceIfNewLower(Integer oldKey, Person newPerson, String ownerName) {
        Person oldPerson = peopleCollection.get(oldKey);
        if (oldPerson.compareTo(newPerson) > 0) {
            if (replacePerson(oldKey, newPerson, ownerName)) {
                return new CommandResult("The element has been replaced");
            }
            return new CommandResult("You don't have access to this element");
        }
        return new CommandResult("The element is lower than a new one or equal");
    }

    public Integer getMatchingIDKey(Integer id) {
        Optional<Integer> element = peopleCollection.entrySet().stream()
                .filter(x -> x.getValue().getId().equals(id))
                .map(Map.Entry::getKey)
                .findFirst();
        return element.orElse(null);
    }

    public CommandResult authorizeUser(String login, String password) {
        try {
            if (checkForLogin(login)) {
                String encryptedPassword = encrypt(password);
                if (checkPassword(login, encryptedPassword)) {
                    CommandResult result = new CommandResult("Your authorization went successful");
                    result.setAuthorized(true);
                    return result;
                }
                return new CommandResult("Wrong password");
            }
            return new CommandResult("Your login aren't registered");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    public CommandResult registerUser(String login, String password) {
        try {
            if (checkForLogin(login)) {
                return new CommandResult("This login has already been registered");
            }
            String encryptedPassword = encrypt(password);
            addUser(login, encryptedPassword);
            CommandResult result = new CommandResult("Your registration went successful");
            result.setAuthorized(true);
            return result;
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    public String encrypt(String message) throws NoSuchAlgorithmException {
        Base64.Encoder encoder = Base64.getEncoder();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] hash = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));
        return encoder.encodeToString(hash);
    }
}
