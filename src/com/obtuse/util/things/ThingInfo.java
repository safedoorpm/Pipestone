/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.things;

import org.jetbrains.annotations.NotNull;
import com.obtuse.ui.tableutils.CheckBoxRowWrapper;

/**
 Created by danny on 2017/10/07.
 */

public interface ThingInfo extends CheckBoxRowWrapper.RowData {

    String getName();

    ThingName getThingName();

    void setThingName( @NotNull ThingName newName );

    String getDescription();

    void setDescription( String description );

}
