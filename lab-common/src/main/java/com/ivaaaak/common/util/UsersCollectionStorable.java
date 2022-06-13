package com.ivaaaak.common.util;

import com.ivaaaak.common.commands.CommandResult;

public interface UsersCollectionStorable {
    CommandResult authorizeUser(String login, String password);
    CommandResult registerUser(String login, String password);
}
