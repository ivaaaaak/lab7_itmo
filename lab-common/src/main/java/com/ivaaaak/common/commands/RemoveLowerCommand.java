package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

public class RemoveLowerCommand extends PrivateAccessCommand implements GeneratedArgumentCommand {

    private final String login = PrivateAccessCommand.getLogin();
    private Person person;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.removeLowerPeople(person, login)) {
            return new CommandResult("Lower elements were removed");
        }
        return new CommandResult("Something went wrong on the server");
    }

    @Override
    public void generateArgument(PersonMaker personMaker) {
        person = personMaker.makePerson();
    }
}
