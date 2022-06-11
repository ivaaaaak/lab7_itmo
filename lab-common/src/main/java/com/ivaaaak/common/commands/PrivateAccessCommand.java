package com.ivaaaak.common.commands;

public abstract class PrivateAccessCommand extends Command {
    private static String mainLogin;

    public static void setLogin(String login) {
        mainLogin = login;
    }

    public static String getLogin() {
        return mainLogin;
    }
}
