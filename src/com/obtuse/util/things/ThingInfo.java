/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.things;

import com.obtuse.ui.tableutils.CheckBoxRowWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by danny on 2017/10/07.
 */

public interface ThingInfo /*extends CheckBoxRowWrapper.RowData*/ {

//    String getName();

    ThingName getThingName();

    void setThingName( @NotNull ThingName newName );

    String getDescription();

    void setDescription( @Nullable String description );

}
