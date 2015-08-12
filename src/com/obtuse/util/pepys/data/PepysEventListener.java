package com.obtuse.util.pepys.data;

import com.obtuse.util.pepys.Pepys;
import org.jetbrains.annotations.NotNull;

/**
 Something which wants to be notified of certain events.
 */

public interface PepysEventListener {

    public void PepysSourceCreated( @NotNull Pepys.PepysEvent event );

    public void PepysSourceChanged( @NotNull Pepys.PepysEvent event );

    public void PepysSourceGone( @NotNull Pepys.PepysEvent event );

}
