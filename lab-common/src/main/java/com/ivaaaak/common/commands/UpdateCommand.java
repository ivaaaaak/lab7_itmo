package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

import java.sql.SQLException;

public class UpdateCommand extends Command implements InputArgumentCommand, GeneratedArgumentCommand {

    private Integer id;
    private Person newPerson;

    public UpdateCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        Integer key = collectionStorage.getMatchingIDKey(id);
        if (key != null) {
            try {
                if (collectionStorage.replacePerson(key, newPerson, getLogin())) {
                    return new CommandResult("The element has been updated");
                }
                return new CommandResult("You don't have access to this element");
            } catch (SQLException e) {
                e.printStackTrace();
                return new CommandResult("Something went wrong on the server");
            }
        }
        return new CommandResult("There's no element with this id. Use \"show\" to get information about elements");
    }

    @Override
    public void generateArgument(PersonMaker personMaker) {
        newPerson = personMaker.makePerson();
    }

    @Override
    public boolean prepareArguments(String arg) {
        Integer possibleId = checkArgument(arg, Integer::parseInt);
        if (possibleId != null) {
            id = possibleId;
            return true;
        }
        return false;
    }
}
