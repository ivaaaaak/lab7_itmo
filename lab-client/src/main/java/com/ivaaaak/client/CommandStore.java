package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.HelpCommand;
import com.ivaaaak.common.commands.InsertCommand;
import com.ivaaaak.common.commands.ShowCommand;
import com.ivaaaak.common.commands.InfoCommand;
import com.ivaaaak.common.commands.FilterStartsWithNameCommand;
import com.ivaaaak.common.commands.RemoveKeyCommand;
import com.ivaaaak.common.commands.ReplaceIfGreaterCommand;
import com.ivaaaak.common.commands.ReplaceIfLowerCommand;
import com.ivaaaak.common.commands.ClearCommand;
import com.ivaaaak.common.commands.UpdateCommand;
import com.ivaaaak.common.commands.RemoveLowerCommand;
import com.ivaaaak.common.commands.MaxByHairColorCommand;
import com.ivaaaak.common.commands.FilterByLocationCommand;

import java.util.HashMap;

public final class CommandStore {

    private final HashMap<String, Command> commands = new HashMap<>();

    public CommandStore(String login, String password) {
        initializeCommands(login, password);
    }

    private void initializeCommands(String login, String password) {
        commands.put("clear", new ClearCommand(login, password));
        commands.put("filter_by_location", new FilterByLocationCommand(login, password));
        commands.put("filter_starts_with_name", new FilterStartsWithNameCommand(login, password));
        commands.put("help", new HelpCommand(login, password));
        commands.put("info", new InfoCommand(login, password));
        commands.put("insert", new InsertCommand(login, password));
        commands.put("max_by_hair_color", new MaxByHairColorCommand(login, password));
        commands.put("remove_key", new RemoveKeyCommand(login, password));
        commands.put("remove_lower", new RemoveLowerCommand(login, password));
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand(login, password));
        commands.put("replace_if_lower", new ReplaceIfLowerCommand(login, password));
        commands.put("show", new ShowCommand(login, password));
        commands.put("update", new UpdateCommand(login, password));
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }
}
