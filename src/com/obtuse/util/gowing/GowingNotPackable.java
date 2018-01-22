/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util.gowing;

/**
 Mark a class which might be {@link GowingPackable} as not actually being packable.
 <p>This is intended for classes which are derived from {@link GowingPackable} classes but which cannot or should not be packable.
 Put another way, a class which is both <em>packable</em> and <em>not packable</em> will be considered to be <em>not packable</em>
 by any compliant {@link GowingPacker} implementation.</p>
 */

public interface GowingNotPackable {

}
