package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

public class AuthorizeCommand extends Command {

    private final String login;
    private final String password;
    private final boolean isRegistered;

    public AuthorizeCommand(String login, String password, boolean isRegistered) {
        this.login = login;
        this.password = password;
        this.isRegistered = isRegistered;
    }

    public CommandResult execute(CollectionStorable collectionStorage) {
        if (isRegistered) {
           return collectionStorage.authorizeUser(login, password);
        }
        return collectionStorage.registerUser(login, password);
    }

}
