package com.ivaaaak.server.Collections;

import com.ivaaaak.common.commands.CommandResult;
import com.ivaaaak.common.util.UsersCollectionStorable;
import com.ivaaaak.server.DataBase.DataBaseManager;
import com.ivaaaak.server.DataBase.UsersTable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class UsersCollectionStorage implements UsersCollectionStorable {

    private final UsersTable usersTable;
    private final ConcurrentHashMap<String, String> usersCollection;

    public UsersCollectionStorage(DataBaseManager dataBaseManager) {
        usersTable = dataBaseManager.getUsersTable();
        usersCollection = usersTable.getCollection();
    }

    public void addUser(String login, String password) throws SQLException {
        usersTable.add(login, password);
        usersCollection.put(login, password);
    }


    public boolean checkLoginAndPassword(String login, String password) throws NoSuchAlgorithmException {
        if (usersCollection.containsKey(login)) {
            String realPassword = usersCollection.get(login);
            String encryptedPassword = encrypt(password);
            return realPassword.equals(encryptedPassword);
        }
        return false;
    }

    public CommandResult authorizeUser(String login, String password) {
        try {
            if (checkLoginAndPassword(login, password)) {
                CommandResult result = new CommandResult("Your authorization went successful");
                result.setAuthorized(true);
                return result;
            }
            return new CommandResult("Wrong login or password");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    public CommandResult registerUser(String login, String password) {
        try {
            if (usersCollection.containsKey(login)) {
                return new CommandResult("This login has already been registered");
            }
            String encryptedPassword = encrypt(password);
            addUser(login, encryptedPassword);
            CommandResult result = new CommandResult("Your registration went successful");
            result.setAuthorized(true);
            return result;
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
            return new CommandResult("Something went wrong on the server");
        }
    }

    public String encrypt(String message) throws NoSuchAlgorithmException {
        Base64.Encoder encoder = Base64.getEncoder();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        byte[] hash = messageDigest.digest(message.getBytes(StandardCharsets.UTF_8));
        return encoder.encodeToString(hash);
    }

}
