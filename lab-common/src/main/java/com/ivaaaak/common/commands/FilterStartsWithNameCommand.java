package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.PeopleCollectionStorable;

public class FilterStartsWithNameCommand extends Command implements InputArgumentCommand {

    private String stringArg;

    public FilterStartsWithNameCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        Person[] answer = collectionStorage.getMatchingPeople(stringArg);
        if (answer.length == 0) {
            return new CommandResult("There aren't any elements whose name starts like this");
        }
        return new CommandResult(answer);
    }

    @Override
    public boolean prepareArguments(String arg) {
        if (!arg.isEmpty()) {
            stringArg = arg;
            return true;
        }
        return false;
    }
}
