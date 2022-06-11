package com.ivaaaak.server;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.util.Pair;
import com.ivaaaak.server.DataBase.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    private static final ConcurrentLinkedQueue<Socket> CLIENTS = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<Pair<Socket, Command>> READY_COMMANDS = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<Pair<Socket, CommandResult>> READY_RESULTS = new ConcurrentLinkedQueue<>();
    private static CollectionStorage collectionStorage;

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
                    collectionStorage = new CollectionStorage(DATA_BASE_MANAGER);
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
                        FIXED_THREAD_POOL.shutdown();
                        CACHED_THREAD_POOL.shutdown();
                        FORK_JOIN_POOL.shutdown();
                        while (!FIXED_THREAD_POOL.isTerminated()) {
                            TimeUnit.NANOSECONDS.sleep(2);
                        }
                        CLIENTS.forEach(Server::closeSocket);
                        break;
                    }
                }
                CLIENTS.removeIf(Socket::isClosed);
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
        try {
            Socket clientSocket = SERVER_EXCHANGER.acceptConnection();
            if (clientSocket != null) {
                CLIENTS.offer(clientSocket);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to open new client socket: ", e);
        }
    }

    private static void readCommands() {
        for (Socket clientSocket : CLIENTS) {
            FIXED_THREAD_POOL.submit(
                    () -> {
                        try {
                            Command newCommand = SERVER_EXCHANGER.receiveCommand(clientSocket);
                            if (newCommand != null) {
                                READY_COMMANDS.offer(new Pair<>(clientSocket, newCommand));
                            }
                        } catch (StreamCorruptedException e) {
                            LOGGER.error("Failed to read a command from client {}: ", clientSocket.getRemoteSocketAddress(), e);
                        } catch (IOException e) {
                            closeSocket(clientSocket);
                        } catch (ClassNotFoundException e) {
                            LOGGER.error("Received invalid data from client {}", clientSocket.getRemoteSocketAddress(), e);
                        }
                    });
        }

    }

    private static void closeSocket(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close client's socket", e);
        }
    }

    private static void executeCommands() {
        Pair<Socket, Command> commandPair = READY_COMMANDS.poll();
        if (commandPair != null) {
            FORK_JOIN_POOL.submit(() -> {
                Socket clientSocket = commandPair.getFirst();
                Command currentCommand = commandPair.getSecond();
                CommandResult result = currentCommand.execute(collectionStorage);
                READY_RESULTS.offer(new Pair<>(clientSocket, result));
            });
        }
    }

    private static void sendResults() {
        Pair<Socket, CommandResult> resultPair = READY_RESULTS.poll();
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
