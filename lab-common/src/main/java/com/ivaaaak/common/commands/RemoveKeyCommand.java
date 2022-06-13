package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.PeopleCollectionStorable;

import java.sql.SQLException;

public class RemoveKeyCommand extends Command implements InputArgumentCommand {

    private Integer key;

    public RemoveKeyCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            try {
                if (collectionStorage.removePerson(key, getLogin())) {
                    return new CommandResult("The element has been removed");
                }
                return new CommandResult("You don't have access to this element");
            } catch (SQLException e) {
                e.printStackTrace();
                return new CommandResult("Something went wrong on the server");
            }
        }
        return new CommandResult("Collection doesn't contain this key");
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
