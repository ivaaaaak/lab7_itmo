package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;

import java.io.Serializable;

public class CommandResult implements Serializable {

    private static final long serialVersionUID = -1218958621582781294L;

    private final String message;
    private final Person[] people;
    private boolean isAuthorized = false;

    public CommandResult(String message, Person[] people) {
        this.message = message;
        this.people = people;
    }
    public CommandResult(String message) {
        this.message = message;
        this.people = null;
    }
    public CommandResult(Person[] people) {
        this.people = people;
        this.message = "";
    }

    public String getMessage() {
        return message;
    }

    public Person[] getPeople() {
        return people;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    @Override
    public String toString() {
        return "CommandResult{"
                + "message='"
                + message
                + '\''
                + '}';
    }
}
