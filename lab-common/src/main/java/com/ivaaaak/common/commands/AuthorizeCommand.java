package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.PeopleCollectionStorable;
import com.ivaaaak.common.util.UsersCollectionStorable;

public class AuthorizeCommand extends Command {

    private UsersCollectionStorable usersCollectionStorage;
    private final boolean isRegistered;

    public AuthorizeCommand(String login, String password, boolean isRegistered) {
        super(login, password);
        this.isRegistered = isRegistered;
    }

    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        if (isRegistered) {
           return usersCollectionStorage.authorizeUser(getLogin(), getPassword());
        }
        return usersCollectionStorage.registerUser(getLogin(), getPassword());
    }

    public void setUsersCollectionStorage(UsersCollectionStorable usersCollectionStorage) {
        this.usersCollectionStorage = usersCollectionStorage;
    }
}
