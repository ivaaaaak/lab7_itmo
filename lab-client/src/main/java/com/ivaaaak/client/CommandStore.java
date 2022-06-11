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

    private static final HashMap<String, Command> COMMANDS = new HashMap<>();

    private CommandStore() {
    }

    static {
        COMMANDS.put("clear", new ClearCommand());
        COMMANDS.put("filter_by_location", new FilterByLocationCommand());
        COMMANDS.put("filter_starts_with_name", new FilterStartsWithNameCommand());
        COMMANDS.put("help", new HelpCommand());
        COMMANDS.put("info", new InfoCommand());
        COMMANDS.put("insert", new InsertCommand());
        COMMANDS.put("max_by_hair_color", new MaxByHairColorCommand());
        COMMANDS.put("remove_key", new RemoveKeyCommand());
        COMMANDS.put("remove_lower", new RemoveLowerCommand());
        COMMANDS.put("replace_if_greater", new ReplaceIfGreaterCommand());
        COMMANDS.put("replace_if_lower", new ReplaceIfLowerCommand());
        COMMANDS.put("show", new ShowCommand());
        COMMANDS.put("update", new UpdateCommand());
    }

    public static HashMap<String, Command> getCommands() {
        return COMMANDS;
    }
}
