package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 Something that has a moniker.
 <p>A moniker is an informal term for someone's or something's name.
 It is used in this context to refer to a name of some instance of something which is likely to be meaningful to a human.
 Not to be confused with the <u>name</u> of some instance which presumably has a more formal connotation.</p>
 */

public interface MonikerOwner {

    /**
     Get this entity's moniker.
     @return this entity's somewhat informal but generally useful name/moniker.
     */

    @NotNull String getMoniker();

}
