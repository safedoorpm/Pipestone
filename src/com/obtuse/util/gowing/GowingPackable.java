package com.obtuse.util.gowing;

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
     to this method on the same particular instance (<i>"equal to"</i> in this context means <i>"has the same type name and entity id"</i>).
     It is presumably obvious that, while there are other approaches which will work, a simple way to meet this
     requirement is to pre-allocate the return value when the instance is created and to have this method's
     implementation always return said pre-allocated instance.<br><br></li>
     <li>return an instance id whose type name is the name of the fully derived class of the particular instance in question.
     It is presumably also obvious that an easy way if not the only way to meet this requirement is to use <code>this.getClass()</code>
     when constructing an instance's instance id.</li>
     </ol>
     Note that constructing the instance id which will be returned by this method using any variant of
     <blockquote><code>private final GowingInstanceId _instanceId = new GowingInstanceId( NameOfImplementingClass.class );</code></blockquote>
     is likely to result in an almost epic <i>"learning experience"</i> someday when the instance being used to call this method is
     of a class which is derived from the class implementing this method (for example, this method is implemented by class A,
     class A is extended by class B and this method,
     implemented by class A and returning an instance id with a type name indicating class A,
     is called on an instance of class B).
     @return this instance's instance id.
     */

    @NotNull
    GowingInstanceId getInstanceId();

    @NotNull
    GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker packer );

    boolean finishUnpacking( GowingUnPacker unPacker );

}
