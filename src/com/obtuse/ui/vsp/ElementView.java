package com.obtuse.ui.vsp;

import com.obtuse.util.UniqueID;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Optional;

/**
 A {@link Component} or a {@link Container} that displays/presents a 'thing' to the human.
 <p>See {@link com.obtuse.ui.vsp.play.PlayElementView} for a very simple example.</p>
 */

public interface ElementView<E extends VirtualScrollableElement> {

    void freshAssignment();

    /**
     A factory for creating {@link ElementView}{@code <E>} instances.
     @param <E> the class which represents a 'thing' which might be
     viewed within a {@link VirtualScrollablePanel}{@code <E>}.
     */

    interface ElementViewFactory<E extends VirtualScrollableElement> {

        ElementView<E> createInstance( VirtualScrollableElementModel<E> elementModel );

    }

    /**
     Fill in this instance from its data model.

     @param dataModel the {@link VirtualScrollableElementModel <E>} to be used to fill this instance.
     This {@code ElementView}'s unique id can be used to determine which element's data model
     the provided view last represented.
     This might speed up the process of filling in the provided {@code ElementView}.
     */

    void fill( @NotNull VirtualScrollableElementModel<E> dataModel );

    /**
     Determine which {@link VirtualScrollableElementModel} this view last represented.

     @return an {@link Optional} containing the {@link UniqueID} of the
     {@link VirtualScrollableElementModel} that
     this view last represented or and empty {@link Optional} if this view has never actually represented anything yet.
     */

    @NotNull
    Optional<UniqueID> getModelUniqueID();

    /**
     Recycle this element view.
     */

    void setElementModel( @NotNull final VirtualScrollableElementModel<E> elementModel );

    /**
     Get this view's model.
     @return this view's model.
     */

    VirtualScrollableElementModel<E> getElementModel();

    /**
     Specify whether this element view should be visible or not right now.

     @param isVisible {@code true} if it should be visible; {@code false} otherwise.
     */

    @SuppressWarnings("unused")
    void setVisible( boolean isVisible );

    /**
     Determine if this element view should be visible right now.

     @return isVisible {@code true} if it should be visible; {@code false} otherwise.
     */

    @SuppressWarnings("unused")
    boolean isVisible();

    Component asComponent();

}
