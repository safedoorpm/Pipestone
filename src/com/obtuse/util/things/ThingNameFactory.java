/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.things;

import com.obtuse.ui.MessageLabel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 Created by danny on 2017/11/17.
 */

public interface ThingNameFactory {

    /**
     Take the name of something in string form and turn it into an actual {@link ThingName}.
     <p/>This is used by the {@link com.obtuse.ui.tableutils.UniqueThingTableCellEditor} when validating newly edited field values
     (a consequence of how {@link javax.swing.table.TableCellEditor}s work).
     @param name the name of the something.
     @return the {@link ThingName} of the something.
     */

    @NotNull
    ThingName makeThingName( String name );

    /**
     Determine if a proposed name syntactically valid.
     @param proposedName the proposed name.
     @return empty {@link Optional} if the name is valid; an {@link Optional} with a suitable error message if the name is invalid.
     */

    @NotNull
    Optional<MessageLabel.AugmentedMessage> validateNameSyntax( String proposedName );

    /**
     Get the name of the type of thing that this factory deals with.
     @param forceCapitalizeFirstLetter {@code true} if the type name will be used in a context where its first letter should probably be capitalized
     (for example, as the first word in a sentence); {@code false} if the intended context is such that it doesn't really matter if the type name
     begins with a capital letter (for example, within the body of a sentence).
     <p>
     For example, if we are dealing with projects then {@code getThingsTypeName( true )} might yield {@code "Project"} whereas
     {@code getThingsTypeName( false )} might yield {@code "project"}.</p>

     @return the name of the type of thing that this factory deals with.
     */

    @NotNull
    String getThingsTypeName( boolean forceCapitalizeFirstLetter );

    /**
     Get the default class for cells in a table column.
     <p/>Used by the {@link com.obtuse.ui.tableutils.FrameworkTableModel.CManager} class.
     @return the default class for cells in a table column.
     */

    @NotNull
    Class<?> getDefaultClass();

    /**
     Get the title for a table column.
     <p/>Used by the {@link com.obtuse.ui.tableutils.FrameworkTableModel.CManager} class.
     @return the title for a table column.
     */

    @NotNull
    String getColumnTitleString();

}
