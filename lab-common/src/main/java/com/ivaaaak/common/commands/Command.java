package com.ivaaaak.common.commands;

import com.ivaaaak.common.util.CollectionStorable;

import java.io.Serializable;

public abstract class Command implements Serializable {

    private static final long serialVersionUID = -8261516019262129082L;

    public abstract CommandResult execute(CollectionStorable collectionStorage);

}
