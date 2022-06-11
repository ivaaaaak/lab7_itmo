package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;


public class HelpCommand extends Command {

    @Override
    public CommandResult execute(CollectionStorable collectionStorage) {
        return new CommandResult("help : вывести справку по доступным командам\n"
                + "info : вывести информацию о коллекции \n"
                + "show : вывести все элементы коллекции в строковом представлении\n"
                + "insert null {element} : добавить новый элемент с заданным ключом\n"
                + "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n"
                + "remove_key null : удалить элемент из коллекции по его ключу\n"
                + "clear : очистить коллекцию\n"
                + "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, как в интерактивном режиме.\n"
                + "exit : завершить программу \n"
                + "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n"
                + "replace_if_greater null {element} : заменить значение по ключу, если новое значение больше старого\n"
                + "replace_if_lower null {element} : заменить значение по ключу, если новое значение меньше старого\n"
                + "max_by_hair_color : вывести любой объект из коллекции, значение поля hairColor которого является максимальным\n"
                + "filter_by_location location : вывести элементы, значение поля location которых равно заданному\n"
                + "filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки");

    }
}
