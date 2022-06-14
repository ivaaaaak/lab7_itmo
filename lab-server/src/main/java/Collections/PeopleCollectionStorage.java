package Collections;

import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.server.DataBase.DataBaseManager;
import com.ivaaaak.server.DataBase.PeopleTable;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PeopleCollectionStorage implements PeopleCollectionStorable {

    private final PeopleTable peopleTable;
    private final ConcurrentHashMap<Integer, Person> peopleCollection;
    private final LocalDate creationDate;

    public PeopleCollectionStorage(DataBaseManager dataBaseManager) {
        peopleTable = dataBaseManager.getPeopleTable();
        creationDate = LocalDate.now();
        peopleCollection = peopleTable.getCollection();
    }

    public ConcurrentHashMap<Integer, Person> getPeopleCollection() {
        return peopleCollection;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void addPerson(Integer key, Person person, String ownerName) throws SQLException {
        person.setKey(key);
        int id = peopleTable.add(person, ownerName);
        person.setId(id);
        peopleCollection.put(key, person);
    }

    public boolean replacePerson(Integer oldKey, Person newPerson, String ownerName) throws SQLException {
        Person oldPerson = peopleCollection.get(oldKey);
        newPerson.setKey(oldKey);
        if (peopleTable.update(oldPerson, newPerson, ownerName) != 0) {
            newPerson.setId(oldPerson.getId());
            peopleCollection.replace(oldKey, oldPerson, newPerson);
            return true;
        }
        return false;
    }

    public boolean removePerson(Integer key, String ownerName) throws SQLException {
        int id = peopleCollection.get(key).getId();
        if (peopleTable.removeByID(id, ownerName) != 0) {
            peopleCollection.remove(key);
            return true;
        }
        return false;
    }

    public void clear(String ownerName) throws SQLException {
        HashSet<Integer> keys = peopleTable.clear(ownerName);
        for (Integer key: keys) {
            peopleCollection.remove(key);
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

    public void removeLowerPeople(Person person, String ownerName) throws SQLException {
       Person[] lowerPeople = peopleCollection.values().stream()
               .filter(x -> x.compareTo(person) < 0)
               .toArray(Person[]::new);
       for (Person p : lowerPeople) {
           if (peopleTable.removeByID(p.getId(), ownerName) != 0) {
               peopleCollection.remove(p.getKey());
           }
       }
    }

    public CommandResult replaceIfNewGreater(Integer oldKey, Person newPerson, String ownerName) throws SQLException {
        Person oldPerson = peopleCollection.get(oldKey);
        if (oldPerson.compareTo(newPerson) < 0) {
            if (replacePerson(oldKey, newPerson, ownerName)) {
                return new CommandResult("The element has been replaced");
            }
            return new CommandResult("You don't have access to this element");
        }
        return new CommandResult("The element is greater than a new one or equal");
    }

    public CommandResult replaceIfNewLower(Integer oldKey, Person newPerson, String ownerName) throws SQLException {
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
}
