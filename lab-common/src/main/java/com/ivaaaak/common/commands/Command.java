package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.PeopleCollectionStorable;

import java.io.Serializable;

public abstract class Command implements Serializable {

    private static final long serialVersionUID = -8261516019262129082L;
    private final String login;
    private final String password;

    public Command(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public abstract CommandResult execute(PeopleCollectionStorable collectionStorage);

}
