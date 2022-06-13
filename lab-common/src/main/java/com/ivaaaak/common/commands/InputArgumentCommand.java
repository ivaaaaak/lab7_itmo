package com.ivaaaak.common.commands;

import java.util.function.Function;

public interface InputArgumentCommand {

    default  <T> T checkArgument(String arg, Function<String, T> converter) {
        if (arg.isEmpty()) {
            System.out.println("This command needs an argument");
            return null;
        }
        try {
            return converter.apply(arg);
        } catch (IllegalArgumentException e) {
            System.out.println("Argument is an integer number. Use \"show\" to get information about elements\n");
            return null;
        }
    }

    boolean prepareArguments(String arg);
}
