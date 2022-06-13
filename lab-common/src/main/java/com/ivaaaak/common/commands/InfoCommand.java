package com.ivaaaak.common.commands;


import com.ivaaaak.common.util.PeopleCollectionStorable;

public class InfoCommand extends Command {

    public InfoCommand(String login, String password) {
        super(login, password);
    }

    @Override
    public CommandResult execute(PeopleCollectionStorable collectionStorage) {
        return new CommandResult("Тип коллекции: "
                + collectionStorage.getPeopleCollection().getClass().toString() + "\n"
                + "Число элементов: " + collectionStorage.getPeopleCollection().size() + "\n"
                + "Дата создания: " + collectionStorage.getCreationDate());

    }
}
