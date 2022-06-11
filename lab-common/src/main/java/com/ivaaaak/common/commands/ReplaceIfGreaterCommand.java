package com.ivaaaak.common.commands;


import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;
import com.ivaaaak.common.util.PersonMaker;


public class ReplaceIfGreaterCommand extends PrivateAccessCommand implements InputArgumentCommand, GeneratedArgumentCommand {

    private final String login = PrivateAccessCommand.getLogin();
    private Integer key;
    private Person person;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            return collectionStorage.replaceIfNewGreater(key, person, login);
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
