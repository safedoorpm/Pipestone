/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.copier;

import org.jetbrains.annotations.NotNull;

/**
 Do that which needs to be done when a copier's arrow is clicked.
 */

public interface CopierBusinessLogic {

    boolean transmogrifyRhsValue( final @NotNull CopierWidget lhsWidget, @NotNull CopierDataSource rhsWidgetAsFilteredSource );

}
