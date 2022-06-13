package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;


public class ShowCommand extends Command {

    public ShowCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().isEmpty()) {
            return new CommandResult("The collection is empty");
        }
        Person[] answer = collectionStorage.getAllPeople();
        return new CommandResult(answer);
    }

}
