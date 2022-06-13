package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;


public class MaxByHairColorCommand extends Command {

    public MaxByHairColorCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        Person maxPerson = collectionStorage.getMaxColorPerson();
        if (maxPerson == null) {
            return new CommandResult("The collection is empty");
        }
        Person[] person = new Person[] {maxPerson};
        return new CommandResult(person);
    }
}
