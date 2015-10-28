package com.obtuse.ui;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 Something capable of showing an error message to the human.
 */

public interface MessageBoardInterface {

    /**
     Show a message to the human.
     <p/>Every message ever posted to this message board has a unique-to-this-message-board id number.
     @param msg the message to display.
     @return the just posted message's id number. Each successive message receives an id number which is one higher than the id number of
     the most recently posted previous message's id number. The first posted message's id number is always 0.
     */

    int postMessage( @NotNull String msg );

    int getOldestValidIdNumber();

    int getNewestValidIdNumber();

    boolean clearAllMessages();

    boolean clearMessages( int oldestIdNumber, int newestIdNumber );

}