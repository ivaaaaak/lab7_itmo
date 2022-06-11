package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

public class ClearCommand extends PrivateAccessCommand {

    private final String login = PrivateAccessCommand.getLogin();

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        if (collectionStorage.clear(login)) {
            return new CommandResult("All your elements have been deleted");
        }
        return new CommandResult("Something went wrong on the server");
    }
}
