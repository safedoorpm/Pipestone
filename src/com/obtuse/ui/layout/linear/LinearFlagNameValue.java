/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import org.jetbrains.annotations.NotNull;

/**
 %%% Something clever goes here.
 */

public class LinearFlagNameValue extends GowingPackableAttribute {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( LinearFlagNameValue.class );
    private static final int VERSION = 1;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @Override
        @NotNull
        public GowingPackable createEntity(
                @NotNull final GowingUnPacker unPacker,
                @NotNull final GowingPackedEntityBundle bundle,
                final GowingEntityReference er
        ) {

            return new LinearFlagNameValue( unPacker, bundle );

        }

    };

    protected LinearFlagNameValue( final LinearFlagName key, final Object value, final GowingPackableType type, final boolean computed ) {

        super( key, value, type, computed );

    }

    public LinearFlagNameValue( final GowingUnPacker unPacker, final GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

    }

    @NotNull
    public GowingPackedEntityBundle bundleThyself( final boolean isPackingSuper, @NotNull final GowingPacker packer ) {

        @SuppressWarnings("UnnecessaryLocalVariable")
        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                LinearFlagNameValue.ENTITY_TYPE_NAME,
                LinearFlagNameValue.VERSION,
                super.bundleThyself( true, packer ),
                packer.getPackingContext()
        );

        return bundle;

    }

}
