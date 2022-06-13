package com.ivaaaak.server;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.util.Pair;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ServerExchanger {

    private static final Logger LOGGER = Server.LOGGER;
    private final ConcurrentLinkedQueue<Socket> clients = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Pair<Socket, Command>> readyCommands = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Pair<Socket, CommandResult>> readyResults = new ConcurrentLinkedQueue<>();
    private final int maxMetaData = 4;
    private final int serverWaitingPeriod = 1000;
    private ServerSocket serverSocket;

    public ServerExchanger() {

    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        LOGGER.info("Listening to the port: {}", serverSocket.getLocalPort());
    }

    public void acceptConnection() throws IOException {
        try {
            serverSocket.setSoTimeout(serverWaitingPeriod);
            Socket clientSocket = serverSocket.accept();
            LOGGER.info("Established connection with client: {}", clientSocket.getRemoteSocketAddress());
            clients.offer(clientSocket);
        } catch (SocketTimeoutException ignored) {}
    }

    public void receiveCommand(Socket clientSocket) throws IOException, ClassNotFoundException {
        InputStream inputStream = clientSocket.getInputStream();
        byte[] commandSize = new byte[maxMetaData];
        try {
            clientSocket.setSoTimeout(serverWaitingPeriod);
            if (inputStream.read(commandSize) != 0) {
                byte[] command = new byte[ByteBuffer.wrap(commandSize).getInt()];
                clientSocket.setSoTimeout(serverWaitingPeriod * 2);
                if (inputStream.read(command) != 0) {
                    Command newCommand = (Command) deserialize(command);
                    LOGGER.info("Read command from the client {}: {}", clientSocket.getInetAddress().toString(), newCommand.toString());
                    readyCommands.offer(new Pair<>(clientSocket, newCommand));
                }
            }
        } catch (SocketTimeoutException ignored) {}
    }

    public void sendResult(Socket clientSocket, CommandResult result) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        byte[] serializedResult = serialize(result);
        int resultSize = serializedResult.length;

        outputStream.write(ByteBuffer.allocate(maxMetaData).putInt(resultSize).array());
        outputStream.write(serializedResult);
        outputStream.flush();
        LOGGER.info("Sent result to the client {}", clientSocket.getRemoteSocketAddress());
    }

    public void closeSocket(Socket clientSocket) {
        try {
            clientSocket.close();
            clients.remove(clientSocket);
        } catch (IOException e) {
            LOGGER.error("Failed to close client's socket", e);
        }
    }

    public Socket getFirstClient() {
        return clients.poll();
    }

    public void putClient(Socket clientSocket) {
        clients.offer(clientSocket);
    }

    public void closeAllClients() {
        clients.forEach(this::closeSocket);
    }

    public Pair<Socket, Command> getReadyCommand() {
        return readyCommands.poll();
    }

    public Pair<Socket, CommandResult> getReadyResult() {
        return readyResults.poll();
    }

    public void putReadyResult(Pair<Socket, CommandResult> resultPair) {
        readyResults.offer(resultPair);
    }
}
