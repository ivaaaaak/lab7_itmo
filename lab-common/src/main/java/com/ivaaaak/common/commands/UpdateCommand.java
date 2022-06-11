package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

public class UpdateCommand extends PrivateAccessCommand implements InputArgumentCommand, GeneratedArgumentCommand {

    private final String login = PrivateAccessCommand.getLogin();
    private Integer id;
    private Person newPerson;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Integer key = collectionStorage.getMatchingIDKey(id);
        if (key != null) {
            if (collectionStorage.replacePerson(key, newPerson, login)) {
                return new CommandResult("The element has been updated");
            }
            return new CommandResult("You don't have access to this element");
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
