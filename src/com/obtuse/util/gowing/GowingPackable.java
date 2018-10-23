package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.holders.GowingStringHolder;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe something that is packable.
 */

public interface GowingPackable {

    /**
     Get this instance's instance id.
     <p/>
     This method should be implemented in a manner which is conceptually equivalent to
     <blockquote>
     <pre>
     private final GowingInstanceId _instanceId = new GowingInstanceId( this.getClass() );

     public final GowingInstanceId getInstanceId() {

     return _instanceId;

     }

     </pre>
     </blockquote>
     The key points are that a call to this method on a particular instance must:
     <ol>
     <li>return a value which is equal to any value returned by any other call
     to this method on the same particular instance (<i>"equal to"</i> in this context means <i>"has the same type name
     and entity id"</i>).
     It is presumably obvious that, while there are other approaches which will work, a simple way to meet this
     requirement is to pre-allocate the return value when the instance is created and to have this method's
     implementation always return said pre-allocated instance.<br><br></li>
     <li>return an instance id whose type name is the name of the fully derived class of the particular instance in question.
     It is presumably also obvious that an easy way if not the only way to meet this requirement is to use <code>this
     .getClass()</code>
     when constructing an instance's instance id.</li>
     </ol>
     Note that constructing the instance id which will be returned by this method using any variant of
     <blockquote><code>private final GowingInstanceId _instanceId = new GowingInstanceId( NameOfImplementingClass.class );
     </code></blockquote>
     is likely to result in an almost epic <i>"learning experience"</i> someday when the instance being used to call this
     method is
     of a class which is derived from the class implementing this method (for example, this method is implemented by class A,
     class A is extended by class B and this method,
     implemented by class A and returning an instance id with a type name indicating class A,
     is called on an instance of class B).

     @return this instance's instance id.
     */

    @NotNull
    GowingInstanceId getInstanceId();

    /**
     Ask a {@link GowingPackable} to pack itself.
     <p>A typical {@code bundleThyself} method should look like this:</p>
     <blockquote>
     <pre>
     &#64;NotNull
     &#64;Override
     public GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper,
            final &#64;NotNull GowingPacker packer
     ) {

         // Create our bundle and ask our parent class to pack itself.
         // This class is derived from GowingAbstractPackableEntity who's 'bundle me' method is called {@code bundleRoot}
         // to ensure that any derivation of GowingAbstractPackableEntity is forced to implement its own
         // bundleThyself method.
         // If your class is derived from any other GowingPackable class then replace "bundleRoot" with "bundleThyself".

         GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
             ENTITY_TYPE_NAME,
             VERSION,
             super.bundleRoot( packer ),
             packer.getPackingContext()
         );

         // Pack a mandatory string (see {@link GowingStringHolder} for more information).

         bundle.addHolder(
                 new GowingStringHolder(
                 THING_NAME,
                 _thingName,
                 true           // indicate that the string is mandatory.
                 )
         );

         // Pack an optional instance of a class that implements GowingPackable.

         bundle.addHolder(
                 new GowingPackableEntityHolder(
                     G_FRED,
                     _fred,
                     packer,
                     false      // indicate that _fred is optional.
                 )
         );

         // Pack a mandatory collection.

         bundle.addHolder(
                 new GowingPackableEntityHolder(
                         G_INBOUND_ITEMS,
                         new GowingPackableCollection<>( _inboundItems ),
                         packer,
                         true
                 )
         );

        return bundle;

     }
     </pre></blockquote>
     @param isPackingSuper {@code true} if the call is being made by a derived class' {@code bundleThyself} method; {@code false} otherwise.
     I, Danny, am not sure why this information might be useful. I do remember believing that it was very useful in
     some context when I wrote the early versions of Gowing.
     <p>Simple rule: if you are writing a {@code bundleThyself} method in user code, <b><u>ALWAYS</u></b>
     set this parameter to {@code true} when you invoke your parent class' {@code bundleThyself} method.
     Since the {@link GowingPacker} implementation(s) call a {@code GowingPackable} class's {@code bundleThyself}
     method with this parameter set to {@code false}, this will allow your {@code bundleThyself} methods to know,
     should they ever care, if they are being called by Gowing directly or
     if they are being called by a descendent class' {@code bundleThyself} method.</p>
     @param packer the {@link GowingPacker} running the packing operation.
     @return the {@link GowingPackedEntityBundle} created by the {@code bundleThyself}.
     Note that EVERY {@code bundleThyself} method in the class hierarchy needs to create and provide its own
     {@code GowingPackedEntityBundle} instance.
     */

    @NotNull
    GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, @NotNull GowingPacker packer );

    /**
     Finish unpacking ourselves.
     @param unPacker the {@link GowingPacker} responsible for this circus.
     @return {@code true} when we're done and don't want to be called again; {@code false} when we're not done and do want to be called again.
     */

    boolean finishUnpacking( @NotNull GowingUnPacker unPacker ) throws GowingUnpackingException;

}
