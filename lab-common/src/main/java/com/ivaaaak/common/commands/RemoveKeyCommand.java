package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

public class RemoveKeyCommand extends PrivateAccessCommand implements InputArgumentCommand {

    private final String login = PrivateAccessCommand.getLogin();
    private Integer key;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.getPeopleCollection().containsKey(key)) {
            if (collectionStorage.removePerson(key, login)) {
                return new CommandResult("The element has been removed");
            }
            return new CommandResult("You don't have access to this element");
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
