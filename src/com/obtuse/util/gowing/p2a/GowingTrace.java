/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing.p2a;

import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.GowingInstanceId;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingUnPacker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Various trace and debug utility methods.
 */

public class GowingTrace {

    private final GowingUnPacker _unPacker;

    private int _maxGerWidth = 20;
    private int _maxGiiWidth = 18;
    private int _maxPrefixMsgWidth = 25;

    public GowingTrace( final @NotNull GowingUnPacker unPacker ) {
        super();

        _unPacker = unPacker;

    }

    public void verboseTrace(
            final @NotNull String msg,
            final @Nullable GowingEntityReference er1,
            final @Nullable GowingEntityReference er2
    ) {

        if ( isVerbose() ) {

            // A message that is being emitted under verbose mode and possibly also for other reasons.

            _maxPrefixMsgWidth = Math.max( _maxPrefixMsgWidth, msg.length() );
            Logger.logMsg( "VERBOSE:  " + ObtuseUtil.rpad( msg, _maxPrefixMsgWidth ) + " " + describeEntity( er1 ) + "; notifying " + describeEntity( er2 ) );

        } else {

            // A message that is emitted under verbose mode and for other reasons but which is being emitted
            // now for other reasons.

            Logger.logMsg( msg );

        }

    }

    public void verboseTrace( final @NotNull String msg, final @Nullable GowingEntityReference er ) {

        if ( isVerbose() ) {

            // A message that is being emitted under verbose mode and possibly also for other reasons.

            _maxPrefixMsgWidth = Math.max( _maxPrefixMsgWidth, msg.length() );
            Logger.logMsg( "VERBOSE:  " + ObtuseUtil.rpad( msg, _maxPrefixMsgWidth ) + " " + describeEntity( er ) );

        } else {

            // A message that is emitted under verbose mode and for other reasons but which is being emitted
            // now for other reasons.

            Logger.logMsg( msg );

        }

    }

    public boolean isVerbose() {

        return _unPacker.isVerbose();

    }

    public void verboseTrace( final @NotNull String msg ) {

        if ( isVerbose() ) {

            // A message that is being emitted under verbose mode and possibly also for other reasons.

            Logger.logMsg( "VERBOSE:  " + msg );

        } else {

            // A message that is emitted under verbose mode and for other reasons but which is being emitted
            // now for other reasons.

            Logger.logMsg( msg );

        }

    }

    public String describeEntity( final GowingEntityReference er ) {

        if ( er == null ) {

            return "<<null entity reference>>";

        } else {

            String erString = er.toString();
            _maxGerWidth = Math.max( _maxGerWidth, erString.length() );
            return ObtuseUtil.rpad( "" + er, _maxGerWidth ) + " " + describeEntity( _unPacker.resolveReference( er ).orElse( null ) );

        }

    }

    @NotNull
    public String describeEntity( final GowingPackable entity ) {

        if ( entity == null ) {

            return "<<null entity>>";

        } else {

            GowingInstanceId instanceId = entity.getInstanceId();
            return describeInstanceId( instanceId );

        }

    }

    @NotNull
    public String describeInstanceId( final GowingInstanceId instanceId ) {

        if ( instanceId == null ) {

            return "<<null instance id>>";

        } else {

            //            Optional<EntityTypeName> optTypeName = _unPackerContext.findTypeByTypeReferenceId(
            //                    instanceId.getTypeId()
            //            );
            //
            //            return instanceId.shortForm() +
            //                   " " +
            //                   (
            //                           optTypeName.isPresent() ?
            //                                   optTypeName.get()
            //                                              .getTypeName() :
            //                                   "<<unknown type (should be impossible)>>"
            //                   );

            String typeName = instanceId.getTypeName();
            String iiString = instanceId.shortForm();
            _maxGiiWidth = Math.max( _maxGiiWidth, iiString.length() );
            return ObtuseUtil.rpad( iiString, _maxGiiWidth ) + " " + typeName;

        }

    }

    @SuppressWarnings("unused")
    public String describeType( final StdGowingTokenizer.GowingToken2 valueToken ) {

        if ( valueToken.type() == StdGowingTokenizer.TokenType.PRIMITIVE_ARRAY ) {

            return valueToken.elementType()
                             .name()
                             .toLowerCase() + "[]";

        } else if ( valueToken.type() == StdGowingTokenizer.TokenType.CONTAINER_ARRAY ) {

            String elementTypeName = valueToken.elementType()
                                               .name();

            return elementTypeName.charAt( 0 ) +
                   elementTypeName.substring( 1 )
                                  .toLowerCase() +
                   "[]";

        } else {

            return valueToken.getObjectValue()
                             .getClass()
                             .getCanonicalName();

        }

    }

    public String toString() {

        return "GowingTrace( " + _unPacker + " )";

    }

}
