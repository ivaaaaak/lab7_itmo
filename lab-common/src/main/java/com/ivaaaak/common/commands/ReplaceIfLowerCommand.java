package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

import java.sql.SQLException;

public class ReplaceIfLowerCommand extends Command implements InputArgumentCommand, GeneratedArgumentCommand {

    private Integer key;
    private Person person;

    public ReplaceIfLowerCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            try {
                return collectionStorage.replaceIfNewLower(key, person, getLogin());
            } catch (SQLException e) {
                e.printStackTrace();
                return new CommandResult("Something went wrong on the server");
            }
        }
        return new CommandResult("Collection doesn't contain this key");
    }

    @Override
    public void generateArgument(PersonMaker personMaker) {
        person = personMaker.makePerson();
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
}
