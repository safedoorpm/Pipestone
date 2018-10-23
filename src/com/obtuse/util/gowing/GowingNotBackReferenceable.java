package com.obtuse.util.gowing;

/*
 * Copyright © 2018 Obtuse Systems Corporation
 */

/**
 Mark a class which might be derived from something which is {@link com.obtuse.util.gowing.p2a.GowingBackReferenceable}
 but which is not actually back-referenceable.
 <p>This is intended for classes which are derived from {@link com.obtuse.util.gowing.p2a.GowingBackReferenceable}
 classes but which cannot or should not be considered to be back-referenceable.
 Put another way, a class which is both <em>back-referenceable</em> and <em>not back-referenceable</em>
 will be considered to be <em>not back-referenceable</em> by any compliant {@link GowingPacker} implementation.</p>
 */

public interface GowingNotBackReferenceable {

}
