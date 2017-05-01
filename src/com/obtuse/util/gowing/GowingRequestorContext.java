package com.obtuse.util.gowing;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a way for a Gowing packer or unpacker to provide some context to their bundling and unbundling code.
 <p/>The software that uses Gowing to do packing or unpacking can set their {@link GowingRequestorContext} into the
 {@link GowingPacker} or {@link GowingUnPacker} via those interface's {@code setGowingRequestorContext} setters.
 They can then retrieve their context later via those same interface's {@code getGowingRequestorContext} getters.
 To avoid potentially mysterious bugs, a packer or an unpacker's {@link GowingRequestorContext} attribute may not
 be changed once it has been set (in other words, plan ahead).
 */

public class GowingRequestorContext {

}
