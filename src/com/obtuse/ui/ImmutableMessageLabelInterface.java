/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

/**
 A {@link MessageLabelInterface} that is immutable.
 */

public interface ImmutableMessageLabelInterface {

    @NotNull
    default Container getAsContainer() {
        return (Container)this;

    }

    @Nullable
    String getText();

    @NotNull
    Optional<String> getOptionalText();

    @NotNull
    Optional<String> getExtraInfo();

}
