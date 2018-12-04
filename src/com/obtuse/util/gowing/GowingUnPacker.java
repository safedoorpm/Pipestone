package com.obtuse.util.gowing;

import com.obtuse.util.ParsingLocation;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackedEntityGroup;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Top level unpacking API.
 */

public interface GowingUnPacker extends Closeable {

    /**
     Enable or disable super verbose mode.
     @param superVerbose if {@code true} then both regular verbose mode and super verbose mode are enabled.
     If {@code false} then super verbose mode is turned off but regular verbose mode is left unaffected.
     While super verbose mode is enabled, at least one message will be logged as each unpacked entity is extracted from the
     pack file) and at least one message will be logged each time an attempt is made to finish an entity (this message will report whether or not the
     attempt to finish the entity actually did finish the entity).
     <p>super verbose mode is intended to be quite comprehensive. Consequently, it can be a lot like drinking from a fire-hose in that you get
     very wet but remain thirsty. If you are using this for diagnostic purposes then you might want to try using {@link #setVerbose(boolean)}
     to enable just the regular verbose mode first. If that doesn't yield enough information, try the unpack again with super verbose mode enabled.</p>
     */

    void setSuperVerbose( boolean superVerbose );

    /*
    Determine if super verbose mode is enabled.
    @return {@code true} if super verbose mode is enabled; {@code false} otherwise.
    Note that super verbose mode being enabled implies that regular verbose mode is also enabled.
     */

    boolean isSuperVerbose();

    /**
     Enable or disable regular verbose mode.
     @param verbose if {@code true} then regular verbose mode is enabled and super verbose mode is disabled.
     if {@code false} then both regular verbose mode and super verbose mode are disabled.
     <p>Unlike the fire-hose effect of enabling super verbose mode, regular verbose mode is intended to be informative without being overwhelming.</p>
     */

    void setVerbose( boolean verbose );

    /**
     Determine if regular verbose mode is enabled.
     @return {@code true} if regular verbose mode is enabled; {@code false otherwise}.
     Note that regular verbose mode being disabled implies that super verbose mode is also disabled.
     */
    boolean isVerbose();

    boolean isClosed();

    /**
     Get the {@link GowingEntityReference} of the entity currently being finished by a call to
     {@link GowingPackable#finishUnpacking(GowingUnPacker)}.
     <p>This method only returns non-null values while {@link GowingPackable#finishUnpacking(GowingUnPacker)} is executing.</p>
     @return {@link GowingEntityReference} of the entity currently being finished by a call to
     {@link GowingPackable#finishUnpacking(GowingUnPacker)}.
     */

    GowingEntityReference getCurrentEntityReference();

    GowingUnPackedEntityGroup unPack() throws GowingUnpackingException, IOException;

    void registerMetaDataHandler( @NotNull GowingMetaDataHandler handler );

    Optional<GowingPackable> resolveReference( GowingEntityReference er );

    @NotNull default GowingPackable resolveMandatoryReference( GowingEntityReference er ) {

        Optional<GowingPackable> optPackable = resolveReference( er );
        if ( optPackable.isPresent() ) {

            return optPackable.get();

        } else {

            throw new NullPointerException( "GowingUnPacker.resolveMandatoryReference:  referenc refers to non-existent entity" );

        }

    }

    ParsingLocation curLoc();

    boolean isFinishingBackReference();

    GowingUnPackerContext getUnPackerContext();

    /**
     Determine if a specified entity has been declared done.
     @param entityReference the entity reference for the entity of interest.
     @return {@code true} if {@code entityReference} is {@code null} or if it has been declared done
     by an earlier return value of {@code true} from its {@link GowingPackable#finishUnpacking(GowingUnPacker)} method.
     */

    boolean isEntityFinished( @Nullable GowingEntityReference entityReference );

    boolean areEntitiesAllFinished( GowingEntityReference... entityReferences );
    boolean areEntitiesAllFinished( @NotNull Collection<GowingEntityReference> entityReferences );

}
