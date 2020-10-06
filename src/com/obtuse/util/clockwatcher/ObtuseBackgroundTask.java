package com.obtuse.util.clockwatcher;

import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/10/22.
 */

public abstract class ObtuseBackgroundTask {

    public enum QueueStyle {
        UNSET,
        NON_STANDARD,
        SEQUENCED,
        SCHEDULED
    }

    public final String purpose;

    private QueueStyle _queueStyle = QueueStyle.UNSET;

    protected ObtuseBackgroundTask( @NotNull final String purpose ) {

        super();

        this.purpose = purpose;

    }
    /* package private */

    void setQueueStyle( @NotNull final QueueStyle queueStyle ) {

        _queueStyle = queueStyle;

    }

    @NotNull
    QueueStyle getQueueStyle() {

        return _queueStyle;

    }

    public abstract void doit();

    public String toString() {

        return "ObtuseBackgroundTask( queueStyle=" + getQueueStyle() + " )";

    }

}
