package net.sourceforge.cobertura.ant;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.util.ShutdownHooks;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;

import java.io.OutputStream;

/**
 * This formatter is used to apply shutdown hooks previously bundled to
 * a static statement on TouchCollector class, so that a shutdown hook
 * and some classes would be loaded when the TouchCollector class would be loaded.
 */
public class CoberturaJUnitResultFormatter implements JUnitResultFormatter {
    @Override
    public void startTestSuite(JUnitTest jUnitTest) throws BuildException {
        //dummy, nothing here
    }

    @Override
    public void endTestSuite(JUnitTest jUnitTest) throws BuildException {
        System.out.println("Running CoberturaJUnitResultFormatter: applying shutdown hooks...");
        ShutdownHooks.saveGlobalProjectDataWithTomcatInits(new ProjectData());
    }

    @Override
    public void setOutput(OutputStream outputStream) {
        //dummy, nothing here
    }

    @Override
    public void setSystemOutput(String s) {
        //dummy, nothing here
    }

    @Override
    public void setSystemError(String s) {
        //dummy, nothing here
    }

    @Override
    public void addError(Test test, Throwable t) {
        //dummy, nothing here
    }

    @Override
    public void addFailure(Test test, AssertionFailedError t) {
        //dummy, nothing here
    }

    @Override
    public void endTest(Test test) {
        //dummy, nothing here
    }

    @Override
    public void startTest(Test test) {
        //dummy, nothing here
    }
}
