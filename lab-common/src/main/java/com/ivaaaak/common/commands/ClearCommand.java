package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.PeopleCollectionStorable;

import java.sql.SQLException;

public class ClearCommand extends Command {

    public ClearCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        try {
            collectionStorage.clear(getLogin());
            return new CommandResult("All your elements have been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }
}
