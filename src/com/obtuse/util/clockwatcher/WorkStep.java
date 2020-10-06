package com.obtuse.util.clockwatcher;

import com.obtuse.util.MonikerOwner;
import org.jetbrains.annotations.NotNull;

/**
 Something that does work on an incremental basis within the Lancot background task mechanism.
 */

public abstract class WorkStep implements MonikerOwner {

    private final String _moniker;

    private WorkStepState _workStepState;

    protected WorkStep(
            @NotNull final String moniker,
            @NotNull final WorkStepState workStepState
    ) {
        super();

        _moniker = moniker;
        _workStepState = workStepState;

    }

    @SuppressWarnings("UnusedReturnValue")
    public abstract boolean doWork( long timeslice );

    @NotNull public String getMoniker() {

        return _moniker;

    }

    public String toString() {

        return "WorkStep( moniker=" + _moniker + " )";

    }

    @SuppressWarnings("unused")
    public WorkStepState getWorkStepState() {

        return _workStepState;

    }

}
