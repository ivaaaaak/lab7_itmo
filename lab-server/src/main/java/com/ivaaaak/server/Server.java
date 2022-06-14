package com.ivaaaak.server;

import com.ivaaaak.common.commands.AuthorizeCommand;
import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.util.Pair;
import Collections.PeopleCollectionStorage;
import Collections.UsersCollectionStorage;
import com.ivaaaak.server.DataBase.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public final class Server {

    static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int ARGUMENTS_NUMBER = 3;
    private static final ServerExchanger SERVER_EXCHANGER = new ServerExchanger();
    private static final DataBaseManager DATA_BASE_MANAGER = new DataBaseManager();

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
    private static final ExecutorService CACHED_THREAD_POOL = Executors.newCachedThreadPool();
    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(5);
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static PeopleCollectionStorage peopleCollectionStorage;
    private static UsersCollectionStorage usersCollectionStorage;

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        if (args.length >= ARGUMENTS_NUMBER) {
            try {
                int serverPort = Integer.parseInt(args[0]);
                String username = args[1];
                String password = args[2];

                try (ServerSocket serverSocket = new ServerSocket(serverPort);
                     Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", username, password)) {
                    SERVER_EXCHANGER.setServerSocket(serverSocket);
                    DATA_BASE_MANAGER.setConnection(connection);
                    LOGGER.info("Successfully made a connection with the database");
                    peopleCollectionStorage = new PeopleCollectionStorage(DATA_BASE_MANAGER);
                    usersCollectionStorage = new UsersCollectionStorage(DATA_BASE_MANAGER);
                    startCycle();
                } catch (IOException e) {
                    LOGGER.error("Failed to open server socket:", e);
                } catch (SQLException e) {
                    LOGGER.error("Failed to open connection with database:", e);
                }
            } catch (NumberFormatException e) {
                LOGGER.error("You must enter port as an integer argument");
            }
        } else {
            LOGGER.error("Enter port, username and password to database as arguments");
        }
    }

    private static void startCycle() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (System.in.available() > 0) {
                    String input = scanner.nextLine();
                    if ("exit".equals(input)) {
                        EXECUTOR.shutdownNow();
                        FIXED_THREAD_POOL.shutdown();
                        FORK_JOIN_POOL.shutdown();
                        CACHED_THREAD_POOL.shutdown();
                        while (!FIXED_THREAD_POOL.isTerminated()) {
                            TimeUnit.NANOSECONDS.sleep(2);
                        }
                        SERVER_EXCHANGER.closeAllClients();
                        break;
                    }
                }
                acceptNewClients();
                readCommands();
                executeCommands();
                sendResults();
            }
        } catch (IOException e) {
            LOGGER.error("Something's wrong with server's console output: ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void acceptNewClients() {
        EXECUTOR.submit(() -> {
            try {
                SERVER_EXCHANGER.acceptConnection();
            } catch (IOException e) {
                LOGGER.error("Failed to open new client socket: ", e);
            }
        });
    }

    private static void readCommands() {
        Socket clientSocket = SERVER_EXCHANGER.getFirstClient();
        if (clientSocket != null) {
            FIXED_THREAD_POOL.submit(
                    () -> {
                        try {
                            SERVER_EXCHANGER.receiveCommand(clientSocket);
                            SERVER_EXCHANGER.putClient(clientSocket);
                        } catch (StreamCorruptedException e) {
                            LOGGER.error("Failed to read a command from client {}: ", clientSocket.getRemoteSocketAddress(), e);
                        } catch (IOException e) {
                            SERVER_EXCHANGER.closeSocket(clientSocket);
                        } catch (ClassNotFoundException e) {
                            LOGGER.error("Received invalid data from client {}", clientSocket.getRemoteSocketAddress(), e);
                        }
                    });
        }
    }

    private static void executeCommands() {
        Pair<Socket, Command> commandPair = SERVER_EXCHANGER.getReadyCommand();
        if (commandPair != null) {
            FORK_JOIN_POOL.submit(() -> {
                Socket clientSocket = commandPair.getFirst();
                Command currentCommand = commandPair.getSecond();
                processCommand(clientSocket, currentCommand);
            });
        }
    }

    private static void processCommand(Socket clientSocket, Command currentCommand) {
        if (currentCommand instanceof AuthorizeCommand) {
            ((AuthorizeCommand) currentCommand).setUsersCollectionStorage(usersCollectionStorage);
            CommandResult result = currentCommand.execute(peopleCollectionStorage);
            SERVER_EXCHANGER.putReadyResult(new Pair<>(clientSocket, result));
        } else {
            try {
                if (usersCollectionStorage.checkLoginAndPassword(currentCommand.getLogin(), currentCommand.getPassword())) {
                    CommandResult result = currentCommand.execute(peopleCollectionStorage);
                    SERVER_EXCHANGER.putReadyResult(new Pair<>(clientSocket, result));
                }
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("Failed to encrypt password", e);
                SERVER_EXCHANGER.putReadyResult(new Pair<>(clientSocket, new CommandResult("Something went wrong on the server")));
            }
        }
    }

    private static void sendResults() {
        Pair<Socket, CommandResult> resultPair = SERVER_EXCHANGER.getReadyResult();
        if (resultPair != null) {
            CACHED_THREAD_POOL.submit(() -> {
                Socket clientSocket = resultPair.getFirst();
                CommandResult result = resultPair.getSecond();
                try {
                    SERVER_EXCHANGER.sendResult(clientSocket, result);
                } catch (IOException e) {
                    LOGGER.error("Failed to send result to the client {}", clientSocket.getRemoteSocketAddress(), e);
                }
            });
        }
    }
}
