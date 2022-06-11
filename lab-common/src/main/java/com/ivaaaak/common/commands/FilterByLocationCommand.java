package com.ivaaaak.common.commands;

import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.CollectionStorable;
import com.ivaaaak.common.util.PersonMaker;

public class FilterByLocationCommand extends Command implements GeneratedArgumentCommand {

    private Location locationArg;

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        Person[] answer = collectionStorage.getMatchingPeople(locationArg);
        if (answer.length == 0) {
            return new CommandResult("There aren't any elements with this location");
        }
        return new CommandResult(answer);
    }

    @Override
    public void generateArgument(PersonMaker personMaker) {
        locationArg = personMaker.makeLocation();
    }
}
