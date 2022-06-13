package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

import java.sql.SQLException;

public class InsertCommand extends Command implements InputArgumentCommand, GeneratedArgumentCommand {

    private Integer key;
    private Person newPerson;

    public InsertCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            return new CommandResult("Collection already have an element with this key");
        }
        try {
            collectionStorage.addPerson(key, newPerson, getLogin());
            return new CommandResult("The element has been added");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    @Override
    public boolean prepareArguments(String arg) {
        Integer possibleKey = checkArgument(arg, Integer::parseInt);
        if (possibleKey != null) {
            key = possibleKey;
            return true;
        }
        return false;
    }


    @Override
    public void generateArgument(PersonMaker personMaker) {
        newPerson = personMaker.makePerson();
    }
}
