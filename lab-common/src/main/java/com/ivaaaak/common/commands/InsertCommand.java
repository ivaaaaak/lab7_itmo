package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;
import com.ivaaaak.common.util.PersonMaker;


public class InsertCommand extends Command implements InputArgumentCommand, GeneratedArgumentCommand {

    private Integer key;
    private Person newPerson;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            return new CommandResult("Collection already have an element with this key");
        }
        if (collectionStorage.addPerson(key, newPerson)) {
            return new CommandResult("The element has been added");
        }
        return new CommandResult("Something went wrong on the server");
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
