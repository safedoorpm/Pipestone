/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Something which does something useful with fatal, error and informational messages.
 <p/>See {@link LoggingMessageProxy} for an extension of this interface.
 */

public interface MessageProxy {

    /**
     Log a final message and then terminate this JVM.
     <p/>This method must not return.
     @param msg the (mandatory) suicide note.
     */

    void fatal( @NotNull String msg );

    /**
     Log a final message with an optional 'appendix' and then terminate this JVM.
     <p/>This method must not return.
     @param msg the (mandatory) suicide note.
     @param appendix the (optional) appendix to the suicide note.
     */

    void fatal( @NotNull String msg, @Nullable String appendix );

    /**
     Log a final message plus an optional 'appendix' in the form of a {@link Throwable} and then terminate this JVM.
     <p/>This method must not return.
     @param msg the (mandatory) suicide note.
     @param e the (optional) appendix to the suicide note.
     */

    void fatal( @NotNull String msg, @Nullable Throwable e );

    /**
     Log a final message with an optional 'appendix' which are optionally annotated by something describing the context of the problem and then terminate this JVM.
     <p/>This method must not return.
     @param msg the (mandatory) suicide note.
     @param appendix the (optional) appendix to the suicide note.
     @param contextName the (optional) name or description of the context within which the problem occurred.
     This should be a string which will guide the analyst of the logged messages to where in the application the problem occurred.
     There are all sorts of possibilities including, for example, a window name with an optional button or field label, or a class name with an optional method name.
     */

    void fatal( @NotNull String msg, @Nullable String appendix, @Nullable String contextName );

    /**
     Log a non-fatal error message.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will almost certainly assume
     that this method will return quite quickly.
     @param msg the (mandatory) error message.
     */

    void error( @NotNull String msg );

    /**
     Log a non-fatal error message along with an optional 'appendix'.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will assume that
     this method will return quite quickly.
     @param msg the (mandatory) error message.
     @param appendix the (optional) appendix.
     */

    void error( @NotNull String msg, @Nullable String appendix );

    /**
     Log a non-fatal error message along with an optional 'appendix' in the form of a {@link Throwable}.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will assume that
     this method will return quite quickly.
     @param msg the (mandatory) error message.
     @param e the (optional) appendix.
     */

    void error( @NotNull String msg, @Nullable Throwable e );

    /**
     Log a non-fatal error message along with an optional 'appendix' which are optionally annotated by something describing the context of the problem.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will assume that
     this method will return quite quickly.
     @param msg the (mandatory) error message.
     @param appendix the (optional) appendix.
     @param contextName the (optional) name or description of the context within which the problem occurred.
     This should be a string which will guide the analyst of the logged messages to where in the application the problem occurred.
     There are all sorts of possibilities including, for example, a window name with an optional button or field label, or a class name with an optional method name.
     */

    void error( @NotNull String msg, @Nullable String appendix, @Nullable String contextName );

    /**
     Log an informational message.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will almost certainly assume
     that this method will return quite quickly.
     <p/>Whatever distinction might exist between an <i>error message</i> and an <i>informational message</i> depends entirely on how
     this method is implemented. Lots of possibilities come to mind including that there might be a way to suppress information messages
     or informational messages might be flagged differently than error messages in log files.
     @param msg the (mandatory) error message.
     */

    void info( @NotNull String msg );

    /**
     Log an informational message with an optional 'appendix' which are optionally annotated by something describing the context of the issue at hand.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will almost certainly assume
     that this method will return quite quickly.
     <p/>Whatever distinction might exist between an <i>error message</i> and an <i>informational message</i> depends entirely on how
     this method is implemented. Lots of possibilities come to mind including that there might be a way to suppress information messages
     or informational messages might be flagged differently than error messages in log files.
     @param msg the (mandatory) error message.
     @param appendix the (optional) appendix.
     */

    void info( @NotNull String msg, @Nullable String appendix );

    /**
     Log an informational message along with an optional 'appendix' which are optionally annotated by something describing the context of the problem.
     <p/>Although this method might decide that keeping the JVM alive is pointless, the caller of this method will almost certainly assume
     that this method will return quite quickly.
     <p/>Whatever distinction might exist between an <i>error message</i> and an <i>informational message</i> depends entirely on how
     this method is implemented. Lots of possibilities come to mind including that there might be a way to suppress information messages
     or informational messages might be flagged differently than error messages in log files.
     @param msg the (mandatory) error message.
     @param appendix the (optional) appendix.
     @param contextName the (optional) name or description of the context within which the issue occurred.
     This should be a string which will guide the analyst of the logged messages to where in the application the problem occurred.
     There are all sorts of possibilities including, for example, a window name with an optional button or field label, or a class name with an optional method name.
     */

    void info( @NotNull String msg, @Nullable String appendix, @Nullable String contextName );

}