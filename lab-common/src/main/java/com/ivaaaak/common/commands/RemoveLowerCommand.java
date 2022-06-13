package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

import java.sql.SQLException;

public class RemoveLowerCommand extends Command implements GeneratedArgumentCommand {

    private Person person;

    public RemoveLowerCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        try {
            collectionStorage.removeLowerPeople(person, getLogin());
            return new CommandResult("Lower elements were removed");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    @Override
    public void generateArgument(PersonMaker personMaker) {
        person = personMaker.makePerson();
    }
}
