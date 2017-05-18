package com.obtuse.util.pepys.data;

import com.obtuse.util.pepys.Pepys;
import org.jetbrains.annotations.NotNull;

/**
 Something which wants to be notified of certain events.
 */

public interface PepysEventListener {

    void PepysSourceCreated( @NotNull Pepys.PepysEvent event );

    void PepysSourceChanged( @NotNull Pepys.PepysEvent event );

    void PepysSourceGone( @NotNull Pepys.PepysEvent event );

}
