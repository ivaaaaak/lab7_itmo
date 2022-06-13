package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.GeneratedArgumentCommand;
import com.ivaaaak.common.commands.InputArgumentCommand;
import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.commands.AuthorizeCommand;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.util.InputManager;
import com.ivaaaak.common.util.PersonMaker;

import java.io.Console;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.StringJoiner;

public final class Client {

    private static boolean isAuthorized = false;
    private static String login;
    private static String password;
    private static ClientExchanger clientExchanger;
    private static final InputManager INPUT_MANAGER = new InputManager();
    private static final PersonMaker PERSON_MAKER = new PersonMaker(INPUT_MANAGER);

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            try {
                String host = args[0];
                int port = Integer.parseInt(args[1]);
                clientExchanger = new ClientExchanger(host, port);

                try (SocketChannel channel = clientExchanger.openChannelToServer()) {
                    clientExchanger.setSocketChannel(channel);
                    while (!isAuthorized) {
                        System.out.println("If you want to end the program type \"exit\"");
                        System.out.println("If you haven't been already registered type \"+\"");
                        String answer = INPUT_MANAGER.readLine();
                        if ("exit".equals(answer)) {
                            return;
                        } else {
                            isAuthorized = authorize(answer);
                        }
                    }
                    startMainCycle();
                } catch (IOException e) {
                    System.err.println("Failed to open channel with server");
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse host and port arguments. Enter them in the order: host's name, port");
            }
        } else {
            System.err.println("You need to enter host's name and port as arguments");
        }
    }

    private static void startMainCycle() {
        CommandStore commandStore = new CommandStore(login, password);
        while (true) {
            String[] command = INPUT_MANAGER.readLine().split(" ");
            String name = command[0];
            String arg = "";
            if (command.length > 1) {
                arg = command[1];
            }
            if ("exit".equals(name)) {
                break;
            }
            if ("execute_script".equals(name)) {
                if (!arg.isEmpty()) {
                    INPUT_MANAGER.connectToFile(arg);
                    continue;
                }
                continue;
            }
            if (commandStore.getCommands().containsKey(name)) {
                Command currentCommand = commandStore.getCommands().get(name);
                if (currentCommand instanceof InputArgumentCommand) {
                    boolean isCorrect = ((InputArgumentCommand) currentCommand).prepareArguments(arg);
                    if (!isCorrect) {
                        continue;
                    }
                }
                if (currentCommand instanceof GeneratedArgumentCommand) {
                    ((GeneratedArgumentCommand) currentCommand).generateArgument(PERSON_MAKER);
                }
                processCommand(currentCommand);
            } else {
                System.out.println("Command not found. Use \"help\" to get information about commands");
            }
        }
    }

    private static void processCommand(Command command) {
        try {
            clientExchanger.sendCommand(command);
            CommandResult result = clientExchanger.receiveResult();
            handleAnswer(result);
        } catch (IOException e) {
            System.err.println("Failed to exchange data with server");
            e.printStackTrace();
            SocketChannel newChanel = clientExchanger.reconnectToServer();
            if (newChanel != null) {
                clientExchanger.setSocketChannel(newChanel);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Incorrect answer from server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean authorize(String answer) {
        AuthorizeCommand authorizeCommand;
        if ("+".equals(answer)) {
            readLoginAndPassword();
            authorizeCommand = new AuthorizeCommand(login, password, false);
        } else {
            readLoginAndPassword();
            authorizeCommand = new AuthorizeCommand(login, password, true);
        }
        try {
            clientExchanger.sendCommand(authorizeCommand);
            CommandResult result = clientExchanger.receiveResult();
            System.out.println(result.getMessage());
            return result.isAuthorized();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void readLoginAndPassword() {
        Console console = System.console();
        System.out.print("Enter login: ");
        login = INPUT_MANAGER.readLine();
        System.out.print("Enter password: ");
        if (console != null) {
            char[] input = console.readPassword();
            password = String.valueOf(input);
        } else {
            password = INPUT_MANAGER.readLine();
        }
    }

    private static void handleAnswer(CommandResult result) {
        String message = result.getMessage();
        Person[] people = result.getPeople();
        if (message != null) {
            System.out.println(message);
        }
        if (people != null) {
            StringJoiner output = new StringJoiner("\n\n");
            for (Person person : people) {
                output.add(person.toString());
            }
            System.out.println(output);
        }
    }

}
