package com.obtuse.util.clockwatcher;

import com.obtuse.util.DateUtils;
import com.obtuse.util.MonikerOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Optional;

/**
 Maintain the current state of a FSM implemented using the {@link WorkStep} mechanism.
 */

public class WorkStepState implements MonikerOwner {

    public enum ProgressType {
        STARTING,
        PROGRESSING,
        DONE
    }

    public interface WorkProgressWatcher {

        void progress( @NotNull final WorkStepState workStepState, @NotNull ProgressType progressType );

    }

    private final String _moniker;
    private WorkStep _workStep;

    private final WorkProgressWatcher _progressWatcher;
    private final int _expectedCount;
    private int _countToDate;
    private int _actualTotalCount;

    private final long _startTime;
    private long _doneTime;

    public WorkStepState(
            @NotNull String moniker,
            final int expectedCount,
            @Nullable final WorkStepState.WorkProgressWatcher progressWatcher
    ) {
        super();

        _moniker = moniker;

        _expectedCount = expectedCount;

        _progressWatcher = progressWatcher;

        _startTime = System.currentTimeMillis();

        maybeReportProgress( ProgressType.STARTING );

    }

    private void maybeReportProgress( @NotNull final WorkStepState.ProgressType progressType ) {

        if ( _progressWatcher != null ) {

            _progressWatcher.progress( this, progressType );

        }

    }

    @SuppressWarnings("unused")
    public int countNewlyDoneItems( final int newlyDoneItemCount ) {

        _countToDate += newlyDoneItemCount;

        maybeReportProgress( ProgressType.PROGRESSING );

        return _countToDate;

    }

    @SuppressWarnings("unused")
    public int getCountToDate() {

        return _countToDate;

    }

    @SuppressWarnings("unused")
    public int getExpectedCount() {

        return _expectedCount;

    }

    @SuppressWarnings("unused")
    public int getActualTotalCount() {

        return _actualTotalCount;

    }

    public boolean isDone() {

        return _doneTime != 0L;

    }

    @SuppressWarnings("unused")
    public void markDone() {

        if ( isDone() ) {

            throw new IllegalArgumentException(
                    "WorkStep:  we are already done " +
                    "(finished at " + DateUtils.formatYYYY_MM_DD_HH_MM_SS_SSS( new Date( _doneTime ) )
            );

        }

        _doneTime = System.currentTimeMillis();
        _actualTotalCount = _countToDate;

        maybeReportProgress( ProgressType.DONE );

    }

    @SuppressWarnings("unused")
    public long getStartTime() {

        return _startTime;

    }

    @SuppressWarnings("unused")
    public long getElapsedTime() {

        if ( _doneTime == 0 ) {

            return System.currentTimeMillis() - _startTime;

        } else {

            return _doneTime - _startTime;

        }

    }

    public Optional<WorkStep> getOptWorkStep() {

        return Optional.ofNullable( _workStep );

    }

    public void setWorkStep( final WorkStep workStep ) {

        _workStep = workStep;

    }

    @Override
    public @NotNull String getMoniker() {

        return _moniker;

    }

    public String toString() {

        return "WorkStepState( " + getMoniker() + ", workStep=" + getOptWorkStep().orElse( null ) + " )";

    }

}
