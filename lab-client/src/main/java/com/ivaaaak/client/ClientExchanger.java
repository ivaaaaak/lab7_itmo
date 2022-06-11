package com.ivaaaak.client;

import com.ivaaaak.common.commands.Command;
import com.ivaaaak.common.commands.CommandResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import static com.ivaaaak.common.util.Serializing.deserialize;
import static com.ivaaaak.common.util.Serializing.serialize;

public class ClientExchanger {

    private final String host;
    private final int port;
    private final int maxMetaData = 4;
    private final int clientWaitingPeriod = 20;
    private SocketChannel channel;

    public ClientExchanger(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setSocketChannel(SocketChannel newChannel) {
        channel = newChannel;
    }

    public SocketChannel openChannelToServer() throws IOException {
        SocketChannel newChannel = SocketChannel.open();
        InetAddress ia = InetAddress.getByName(host);
        InetSocketAddress address = new InetSocketAddress(ia, port);
        newChannel.connect(address);
        System.out.println("Connection with server " + ia.getHostName() + " has been established");
        newChannel.configureBlocking(false);
        return newChannel;
    }

    public SocketChannel reconnectToServer() {
        try {
            return openChannelToServer();
        } catch (IOException e) {
            return null;
        }
    }

    public void sendCommand(Command command) throws IOException, InterruptedException {
        byte[] serializedCommand = serialize(command);
        int commandSize = serializedCommand.length;

        ByteBuffer mainData = ByteBuffer.wrap(serializedCommand);
        ByteBuffer metaData = ByteBuffer.allocate(maxMetaData).putInt(commandSize);
        ((Buffer) metaData).rewind();

        int waitingTime = clientWaitingPeriod;
        while (waitingTime > 0) {
            if (channel.write(metaData) == metaData.remaining()) {
                while (waitingTime > 0) {
                    if (channel.write(mainData) != mainData.remaining()) {
                        waitingTime--;
                        TimeUnit.MILLISECONDS.sleep(clientWaitingPeriod);
                    } else {
                        return;
                    }
                }
            }
            waitingTime--;
            TimeUnit.MILLISECONDS.sleep(clientWaitingPeriod);
        }
        System.err.println("Failed to send the command");
    }

    public CommandResult receiveResult() throws IOException, ClassNotFoundException, InterruptedException {
        ByteBuffer metaData = ByteBuffer.allocate(maxMetaData);

        int waitingTime = clientWaitingPeriod;
        while (waitingTime > 0) {
            if (channel.read(metaData) == metaData.remaining()) {
                ((Buffer) metaData).rewind();
                ByteBuffer mainData = ByteBuffer.allocate(metaData.getInt());
                while (waitingTime > 0) {
                    if (channel.read(mainData) == mainData.remaining()) {
                        return (CommandResult) deserialize(mainData.array());
                    }
                    waitingTime--;
                    TimeUnit.MILLISECONDS.sleep(clientWaitingPeriod);
                }
            }
            waitingTime--;
            TimeUnit.MILLISECONDS.sleep(clientWaitingPeriod);
        }
        return new CommandResult("Waiting time for the response from server has expired");
    }



}
