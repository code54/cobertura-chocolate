package boca.corba;

import org.omg.CORBA.ORB;

import java.io.PrintStream;

/**
 * This source file is like Test88.java but is stored in UTF-16 encoding.
 *
 * @version     1.14, 05/11/10
 * @since       JDK1.2
 */

public interface TestEncoding
{
    String getCommandName();

    public final static boolean shortHelp = true;
    public final static boolean longHelp  = false;

    void printCommandHelp(PrintStream out, boolean helpType);

    public final static boolean parseError = true;
    public final static boolean commandDone = false;

    boolean processCommand(String[] cmd, ORB orb, PrintStream out);
}
