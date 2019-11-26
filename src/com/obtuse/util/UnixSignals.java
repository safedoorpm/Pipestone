package com.obtuse.util;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 Hopefully useful information about Unix signals.
 <p>Based on /usr/include/sys/signal.h from an OpenBSD system c. 2016.</p>
 */

@SuppressWarnings("unused")
public class UnixSignals {

 /** hangup */
    public static final int SIGHUP = 1;
 /** interrupt */
    public static final int SIGINT = 2;
 /** quit */
    public static final int SIGQUIT = 3;
 /** illegal instruction (not reset when caught) */
    public static final int SIGILL = 4;
 /** trace trap (not reset when caught) */
    public static final int SIGTRAP = 5;
    // #if __BSD_VISIBLE
 /**
  PDP-11 IOT instruction executed (compatibility)
    <p>Note that signal 6 is both SIGIOT and SIGABRT.
    Since it gets awkward if I include both signals in these mappings,
    I feel that I must ignore one of them.
  Despite the fact that Sixth Edition of Unix (c. 1975) has a SIGIOT signal and does not have a SIGABRT signal
  (i.e. historically, SIGIOT should take precedence), SIGABRT is the one that I'm going to put into the
  mapping tables below because my sense is that SIGABRT is more 'relevant' in the modern era than SIGIOT.
  Your mileage and opinions may vary.
    </p>
  */
    public static final int SIGIOT = 6;
 /** abort() system call has been invoked */
    public static final int SIGABRT = SIGIOT;
    // #endif
 /** EMT instruction */
    public static final int SIGEMT = 7;
 /** floating point exception */
    public static final int SIGFPE = 8;
 /** kill (cannot be caught or ignored) */
    public static final int SIGKILL = 9;
 /** bus error */
    public static final int SIGBUS  = 10;
 /** segmentation violation */
    public static final int SIGSEGV = 11;
 /** bad argument to system call */
    public static final int SIGSYS  = 12;
 /** write on a pipe with no one to read it */
    public static final int SIGPIPE = 13;
 /** alarm clock */
    public static final int SIGALRM = 14;
 /** software termination signal from kill */
    public static final int SIGTERM = 15;
 /** urgent condition on IO channel */
    public static final int SIGURG  = 16;
 /** sendable stop signal not from tty */
    public static final int SIGSTOP = 17;
 /** stop signal from tty */
    public static final int SIGTSTP = 18;
 /** continue a stopped process */
    public static final int SIGCONT = 19;
 /** to parent on child stop or exit */
    public static final int SIGCHLD = 20;
 /** to readers pgrp upon background tty read */
    public static final int SIGTTIN = 21;
 /** like TTIN for output if (tp->t_local&LTOSTOP) */
    public static final int SIGTTOU = 22;
 /** input/output possible signal */
    //    #if __BSD_VISIBLE
    public static final int SIGIO = 23;
    //    #endif
 /** exceeded CPU time limit */
    public static final int SIGXCPU = 24;
 /** exceeded file size limit */
    public static final int SIGXFSZ = 25;
 /** virtual time alarm */
    public static final int SIGVTALRM = 26;
 /** profiling time alarm */
    //    #if __BSD_VISIBLE
    public static final int SIGPROF = 27;
 /** window size changes */
    public static final int SIGWINCH = 28;
 /** information request */
    public static final int SIGINFO = 29;
    //    #endif
 /** user defined signal 1 */
    public static final int SIGUSR1 = 30;
 /** user defined signal 2 */
    public static final int SIGUSR2 = 31;
    //    #if __BSD_VISIBLE
     /** thread library AST */
    public static final int SIGTHR  = 32;
    //    #endif

    public static final SortedMap<Integer,String> s_signal2name;
    public static final SortedMap<String,Integer> s_name2signal;
    static {

        SortedMap<Integer,String> signal2name = new TreeMap<>();
        signal2name.put( SIGHUP, "SIGHUP"); /* hangup */
        signal2name.put( SIGINT, "SIGINT"); /* interrupt */
        signal2name.put( SIGQUIT, "SIGQUIT"); /* quit */
        signal2name.put( SIGILL, "SIGILL"); /* illegal instruction (not reset when caught) */
        signal2name.put( SIGTRAP, "SIGTRAP"); /* trace trap (not reset when caught) */
        signal2name.put( SIGIOT, "SIGIOT"); /* abort() */
        // #if __BSD_VISIBLE
//        signals.put(SIGIOT, "SIGIOT]" = SIGABRT; /* compatibility */
        signal2name.put( SIGEMT, "SIGEMT"); /* EMT instruction */
        // #endif
        signal2name.put( SIGFPE, "SIGFPE"); /* floating point exception */
        signal2name.put( SIGKILL, "SIGKILL"); /* kill (cannot be caught or ignored) */
        signal2name.put( SIGBUS, "SIGBUS"); /* bus error */
        signal2name.put( SIGSEGV, "SIGSEGV"); /* segmentation violation */
        signal2name.put( SIGSYS, "SIGSYS"); /* bad argument to system call */
        signal2name.put( SIGPIPE, "SIGPIPE"); /* write on a pipe with no one to read it */
        signal2name.put( SIGALRM, "SIGALRM"); /* alarm clock */
        signal2name.put( SIGTERM, "SIGTERM"); /* software termination signal from kill */
        signal2name.put( SIGURG, "SIGURG"); /* urgent condition on IO channel */
        signal2name.put( SIGSTOP, "SIGSTOP"); /* sendable stop signal not from tty */
        signal2name.put( SIGTSTP, "SIGTSTP"); /* stop signal from tty */
        signal2name.put( SIGCONT, "SIGCONT"); /* continue a stopped process */
        signal2name.put( SIGCHLD, "SIGCHLD"); /* to parent on child stop or exit */
        signal2name.put( SIGTTIN, "SIGTTIN"); /* to readers pgrp upon background tty read */
        signal2name.put( SIGTTOU, "SIGTTOU"); /* like TTIN for output if (tp->t_local&LTOSTOP) */
//    #if __BSD_VISIBLE
        signal2name.put( SIGIO, "SIGIO"); /* input/output possible signal */
//    #endif
        signal2name.put( SIGXCPU, "SIGXCPU"); /* exceeded CPU time limit */
        signal2name.put( SIGXFSZ, "SIGXFSZ"); /* exceeded file size limit */
        signal2name.put( SIGVTALRM, "SIGVTALRM"); /* virtual time alarm */
        signal2name.put( SIGPROF, "SIGPROF"); /* profiling time alarm */
//    #if __BSD_VISIBLE
        signal2name.put( SIGWINCH, "SIGWINCH"); /* window size changes */
        signal2name.put( SIGINFO, "SIGINFO"); /* information request */
//    #endif
        signal2name.put( SIGUSR1, "SIGUSR1"); /* user defined signal 1 */
        signal2name.put( SIGUSR2, "SIGUSR2"); /* user defined signal 2 */
//    #if __BSD_VISIBLE
        signal2name.put( SIGTHR, "SIGTHR");     /* thread library AST */
//    #endif

        SortedMap<String,Integer> name2signal = new TreeMap<>();
        signal2name.forEach(
                ( signal, name ) -> name2signal.put( name, signal )
        );

        s_signal2name = Collections.unmodifiableSortedMap( signal2name );
        s_name2signal = Collections.unmodifiableSortedMap( name2signal );

        if ( s_signal2name.size() != s_name2signal.size() ) {

            throw new IllegalArgumentException(
                    "ObtuseUtil:  signal2name map has " + s_signal2name.size() +
                    " entries but name2signal map has " + s_name2signal.size() + " entries"
            );

        }
    }

    // Some hopefully useful functions for taking about Unix process exit status
    // what is returned by {@link Process#waitFor}.

    public static boolean wIfStopped( int exitStatus ) {

        return ( exitStatus & 0xff ) == 0x7f;

    }

    public static int wStopSig( int exitStatus ) {

        return ( exitStatus >> 8 ) & 0xff;

    }

    public static boolean exited( int exitStatus ) {

        int wStatus = exitStatus & 0x7f;
        return wStatus == 0;

    }

    public static boolean signalled( int exitStatus ) {

        int wStatus = exitStatus & 0x7f;
        return !wIfStopped( exitStatus ) && wStatus != 0;

    }

    public static int wTermSig( int exitStatus ) {

        int wStatus = exitStatus & 0x7f;
        return wStatus;

    }

    public static int wExitStatus( int exitStatus ) {

        return ( ( exitStatus >> 8 ) & 0xff );

    }

    @SuppressWarnings("UnusedReturnValue")
    public static String explainExitStatus( int exitStatus ) {

        boolean stopped = ( exitStatus & 0xff ) == 0x7f;
        int wStatus = exitStatus & 0x7f;
        boolean exited = wStatus == 0;
        boolean signalled = wStatus != 0x7f && wStatus != 0;

        String explanation;
        if ( stopped ) {

            int sigNumber = ( exitStatus >> 8 ) & 0x7f;
            String sigName = s_signal2name.get( sigNumber );
            if ( sigName == null ) {

                sigName = "unknown";

            }
            explanation = "stopped by signal " + sigNumber + " (" + sigName + ")";

        } else if ( exited ) {

            explanation = "exited (status=" + ( ( exitStatus >> 8 ) & 0xff ) + ")";

        } else if ( signalled ) {

            int sigNumber = exitStatus & 0x7f;
            String sigName = s_signal2name.get( sigNumber );
            if ( sigName == null ) {

                sigName = "unknown";

            }
            explanation = "terminated by signal " + sigNumber + " (" + sigName + ")";

        } else {

            byte[] hv = new byte[2];
            hv[0] = (byte)( ( exitStatus >> 8 ) & 0xff );
            hv[1] = (byte)( exitStatus & 0xff );
            explanation = "exited for unknown reason (exitStatus=0x" + ObtuseUtil.hexvalue( hv ) + ")";

            ObtuseUtil.doNothing();

        }

        return explanation;

//        // IntelliJ IDEA believes that this is unreachable code.
//        // Call me skeptical but if we get here then something is really really wrong!
//
//        byte[] hv = new byte[2];
//        hv[0] = (byte)( ( exitStatus >> 8 ) & 0xff );
//        hv[1] = (byte)( exitStatus & 0xff );
//        throw new HowDidWeGetHereError( "explainExitStatus:  how did we get here (exitStatus=0x" + ObtuseUtil.hexvalue( hv ) + ")" );

    }


}
