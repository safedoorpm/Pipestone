package com.obtuse.util.gowing.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.holders.GowingPackableEntityHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 %%% Something clever goes here.
 */

public class GowingUtil {

    /**
     Effectively mark this class as a utility class.
     */

    private GowingUtil() {

        super();

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static Optional<String> checkActuallyPackable( final @NotNull GowingPackable entity ) {

        if ( entity instanceof GowingNotPackable ) {

            return Optional.of( "marked GowingNotPackable" );

        }

        return Optional.empty();

    }

    public static boolean isActuallyPackable( final @NotNull GowingPackable entity ) {

        return !checkActuallyPackable( entity ).isPresent();

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static Optional<String> checkActuallyBackReferenceable( final @NotNull GowingPackable entity ) {

        Optional<String> optReason = checkActuallyPackable( entity );
        if ( optReason.isPresent() ) {

            return optReason;

        } else if ( !( entity instanceof GowingBackReferenceable ) ) {

            return Optional.of( "not marked GowingBackReferenceable" );

        } else if ( entity instanceof GowingNotBackReferenceable ) {

            return Optional.of( "marked GowingNotBackReferenceable" );

        }

        return Optional.empty();

    }

    public static boolean isActuallyBackReferenceable( final @NotNull GowingPackable entity ) {

        return !checkActuallyBackReferenceable( entity ).isPresent();

    }

    @SuppressWarnings("unused")
    public static String describeGowingEntitySafely( final GowingPackable packable ) {

        if ( packable == null ) {

            return "null";

        }

        try {

            return packable.toString();

        } catch ( Throwable e ) {

            return "safeDescription( " + packable.getClass().getCanonicalName() + ")";

        }

    }

    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static void main( final String[] args ) {

        Byte[] a = new Byte[]{ (byte)1, null, (byte)3, (byte)4 };

        doit( a );

    }

    public static void doit( @Nullable final Byte@NotNull[] a ) {

        System.out.print( '[' );
        String comma = "";
        for ( Byte b : a ) {

            System.out.print( comma );
            comma = ",";

            if ( b == null ) {

                System.out.print( GowingConstants.NULL_VALUE );

            } else {

                int ll = b.intValue();
                int high = ( ll >> 4 ) & 0xf;
                int low = ll & 0xf;

                System.out.print( HEX_CHARS[high] );
                System.out.print( HEX_CHARS[low] );

            }

        }

        System.out.print( ']' );
        System.out.println();

    }

    public static <T> T mustBe(
            final @NotNull String methodName,
            final @NotNull String entityColloquialName,
            final @NotNull Class<T> expectedClass,
            @Nullable final Object entity
    ) {

        if ( entity == null ) {

            return null;

        }

        if ( expectedClass.isAssignableFrom( entity.getClass() ) ) {

            @SuppressWarnings("unchecked") T rval = (T)entity;

            return rval;

        }

        getGrumpy( methodName, entityColloquialName, expectedClass, entity );

        return null;

    }

    public static void getGrumpy(
            final @NotNull String methodName,
            final @NotNull String entityColloquialName,
            final @NotNull Class<?> expectedClass,
            @Nullable final Object entity
    ) {

        ObtuseUtil.getGrumpy( methodName, "finish unpacking", entityColloquialName, expectedClass, entity );

    }

    public static void verifyActuallyPackable( final @NotNull String who, @Nullable final EntityName what, final @NotNull GowingPackable entity ) {

        Optional<String> optReason = checkActuallyPackable( entity );

        if ( optReason.isPresent() ) {

            throw new IllegalArgumentException( who + "):  " + what + " is not actually packable - entity=" + entity + ", reason=" + optReason.get() );

        }

    }

    public static String describeClassInstance( final Object obj ) {

        if ( obj == null ) {

            return "null";

        } else {

            return obj.getClass().getCanonicalName();

        }

    }

    /**
     Pack what is either a {@link String} or a {@link GowingPackable}.
     <p>This is the first half of the illusion of sorts that the Java {@code String} type is {@code GowingPackable}.</p>
     @param who who's called (used if an exception is about to be thrown).
     @param what what the thing is (also used if an exception is about to be thrown).
     @param packer the {@link GowingPacker} that's running this railroad.
     @param bundle the {@link GowingPackedEntityBundle} that we are packing things into.
     @param entityName the {@link EntityName} of the thing being packed.
     @param thing the thing itself.
     @param <T> what the thing is.
     */

    public static <T> void packMaybeString(
            final @NotNull String who,
            final @NotNull String what,
            final @NotNull GowingPacker packer,
            final GowingPackedEntityBundle bundle,
            final @NotNull EntityName entityName,
            final @NotNull T thing
    ) {

        if ( thing instanceof String ) {

            bundle.addHolder( new GowingPackableEntityHolder( entityName, new GowingString( (String)thing ), packer, true ) );

        } else if ( thing instanceof GowingPackable ) {

            bundle.addHolder( new GowingPackableEntityHolder( entityName, (GowingPackable)thing, packer, true ) );

        } else {

            throw new HowDidWeGetHereError( who + ":  " + what + " must be a String or GowingPackable - this one is " + thing.getClass() );

        }

    }

    /**
     Pack what was previously packed by {@link #packMaybeString(String, String, GowingPacker, GowingPackedEntityBundle, EntityName, Object)}.
     <p>This is the second half of the illusion of sorts that the Java {@code String} type is {@code GowingPackable}.</p>
     @param who who's called (used if an exception is about to be thrown).
     @param what what the thing is (also used if an exception is about to be thrown).
     @param unPacker the {@link GowingUnPacker} that's running this railroad.
     @param er the {@link GowingEntityReference} that describing the entity that we're unpacking.
     @param idClass the {@link Class}{@code <?>} that the unpacked thing should be (see note below).
     @param <T> what the thing is.
     @return the unpacked thing (will have been verified to actually be of the class specified by {@code idClass}.
     <p>
     <b>Note:</b>
     So . . . the obvious question is why does this method have a parameter which specifies what the type of the packed thing actually is?
     Well . . . this method and its cousin {@link #packMaybeString(String, String, GowingPacker, GowingPackedEntityBundle, EntityName, Object)}
     are actually intended to be used when packing and unpacking a parameterized class where the author of the class might not directly
     know what the class of the thing being packed is but where the user of the class is willing to tell the class (presumably via a parameter in
     the parameterized class' constructor).
     For example, consider the class
     <blockquote>
     <tt>public class Hello&lt;T&gt {<blockquote>
         T _tThing;<br>
         .<br>
         .<br>
         .<br>
         public Hello( Class<?> tThingClass</?>, <i>other parameters go here</i> ) {<blockquote>
             super();<br>
     <br>
             _tThingClass = tThingClass;<br>
             .<br>
             .<br>
             .</blockquote>
         }</blockquote>
     }
     </tt>
     </blockquote>
     Java implements such a class by declaring {@code _tThing} to be of class {@code Object}.
     It then does what type-checking it can where the class is used.
     Someone trying to make the above class {@link GowingPackable} will need to be able to pack and unpack {@code _tThing}.
     If {@code T} can be anything at all then it isn't actually possible to make the above class {@code GowingPackable}.
     Fortunately, it sometimes turns out that one knows that {@code T} is either {@code String} or some {@code GowingPackable} class.
     That's where this method and its cousin {@link #packMaybeString(String, String, GowingPacker, GowingPackedEntityBundle, EntityName, Object)}
     come in.
     The {@link GowingPackable#bundleThyself(boolean, GowingPacker)} method for the class would pack {@code _tThing} by invoking
     {@link #packMaybeString(String, String, GowingPacker, GowingPackedEntityBundle, EntityName, Object)} roughly as follows:
     <blockquote>{@code GowingUtil.packMaybeString( _who, "_tThing", packer, bundle, G_FIELD, _tThing );}</blockquote>
     The {@link GowingPackable#finishUnpacking(GowingUnPacker)} method would then unpack {@code _tThing} by invoking this method
     roughly as follows:
     <blockquote>{@code _tThing = GowingUtil.unpackMaybeString( who(), "_tThing", unPacker, _tThingReference, _tThingClass );}
     </blockquote>
     The argument {@code _tThingClass} in that last line of code is the 'magic' that makes this work (it isn't exactly powerful magic but it is just
     enough to be useful in some situations). Somehow, probably via the {@code Hello} class' constructor, each {@code Hello}
     instance knows the actual class of its {@code _tThing} field.
     Assuming that the actual class of its {@code tThing} field is held in a field declared as follows:
     <blockquote>{@code private final Class<?> _tThingClass;}</blockquote>, this method can be used as follows to recover (i.e. unpack)
     the instance's {@code _tThing} field using the above invocation of this method.
     <p>Like I said, the magic isn't very powerful but it does allow {@code _tField} to be packed and unpacked in a type-tight fashion.</p>
     </p>
     <p>My long term plan is to have variants of these methods for the other built-in Java container types like {@link Integer}, {@link Double}, etc.
     They will work in future variants of the existing {@link GowingString} class to make it possible to create type-tight {@code GowingPackable}
     parameterized classes where a parameter might be one of these other Java container classes or 'just' a regular {@link GowingPackable} class.</p>
     <p>Finally, if you don't want to deal with the hassles of making this method work for you, you might find
     {@link #unpackMaybeString(GowingUnPacker, GowingEntityReference)} useful as it can be used to do what the above example does but in a type-loose
     fashion.</p>
     */

    public static <T> T unpackMaybeString(
            final @NotNull String who,
            final @NotNull String what,
            final @NotNull GowingUnPacker unPacker,
            final @Nullable GowingEntityReference er,
            final @NotNull Class<?> idClass
    ) {

        @SuppressWarnings("unchecked") T rval = (T)unpackMaybeString( unPacker, er );

        if ( rval == null || !idClass.isAssignableFrom( rval.getClass() ) ) {

            return rval;

        }

        throw new IllegalArgumentException(
                who + ":  " + what + " is supposed to be " + idClass.getSimpleName() +
                " but it is " + rval.getClass().getSimpleName()
        );

    }

    /**
     Pack what was previously packed by {@link #packMaybeString(String, String, GowingPacker, GowingPackedEntityBundle, EntityName, Object)}.
     <p>This is the second half of the illusion of sorts that the Java {@code String} type is {@code GowingPackable}.</p>
     @param unPacker the {@link GowingUnPacker} that's running this railroad.
     @param er the {@link GowingEntityReference} that describing the entity that we're unpacking.
     @return a {link String} if the packed entity was a {@code String}. Otherwise, a {link GowingPackable}.
     */

    public static Object unpackMaybeString( final @NotNull GowingUnPacker unPacker, final @Nullable GowingEntityReference er ) {

        if ( er == null ) {

            return null;

        }

        GowingPackable id = unPacker.resolveMandatoryReference( er );
        Object rval;
        if ( id instanceof GowingString ) {

            rval = ( (GowingString)id ).string;

        } else {

            rval = id;

        }

        return rval;

    }

    /**
     If an object is a {@link GowingEntityReference} then use {@link GowingUnPacker#resolveReference(GowingEntityReference)} to get the entity;
     otherwise, return the object. This allows us to support keys and values which might be GowingPackable (in which case the respective reference
     will be a {@link GowingEntityReference}) or instances of the various Java scalar container classes like {@link Integer} and {@link String}
     (not always considered a Java scalar container class but it is for our purposes).
     <p/>This may seem like a rather specialized operation but the use case actually comes up fairly often.

     @param unPacker the unpacker that can resolve {@link GowingEntityReference}s.
     @param value    the object in question.
     @return the object of interest.
     */

    public static Object fetchActualValue( final GowingUnPacker unPacker, final Object value ) {

        if ( value instanceof GowingEntityReference ) {

            Object rv1 = unPacker.resolveReference( (GowingEntityReference)value ).orElse( null );
            return rv1;

        } else {

            return value;

        }

    }

}
