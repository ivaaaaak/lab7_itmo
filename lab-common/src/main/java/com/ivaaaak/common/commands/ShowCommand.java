package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;


public class ShowCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().isEmpty()) {
            return new CommandResult("The collection is empty");
        }
        Person[] answer = collectionStorage.getAllPeople();
        return new CommandResult(answer);
    }

}
