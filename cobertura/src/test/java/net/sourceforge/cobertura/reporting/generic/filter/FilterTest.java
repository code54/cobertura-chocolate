package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.CoverageData;
import net.sourceforge.cobertura.reporting.generic.GenericReportEntry;
import net.sourceforge.cobertura.reporting.generic.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FilterTest {

    private Node report;

    private static final String REPORT_NAME = "report";
    private static final String PROJECT_NAME = "test-project";
    private static final String PACKAGE_NAME = "testpackage";
    private static final String SOURCE_NAME = "SomeClass.java";
    private static final String CLASS_NAME = "SomeClass";
    private static final String LINE01_NAME = "1";
    private static final String LINE02_NAME = "2";
    private static final String LINE03_NAME = "3";


    @Before
    public void setUp(){
        report = new GenericReportEntry(GenericReportEntry.report, REPORT_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node project = new GenericReportEntry(GenericReportEntry.project, PROJECT_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node packag = new GenericReportEntry(GenericReportEntry.packag, PACKAGE_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node source = new GenericReportEntry(GenericReportEntry.sourcefile, SOURCE_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node clazz = new GenericReportEntry(GenericReportEntry.clazz, CLASS_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node line01 = new GenericReportEntry(GenericReportEntry.line, LINE01_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node line02 = new GenericReportEntry(GenericReportEntry.line, LINE02_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        Node line03 = new GenericReportEntry(GenericReportEntry.line, LINE03_NAME,
                mock(CoverageData.class), mock(CoverageData.class), 0, 0);

        report.addNode(GenericReportEntry.project, project);
        project.addNode(GenericReportEntry.packag, packag);
        packag.addNode(GenericReportEntry.sourcefile, source);
        source.addNode(GenericReportEntry.clazz, clazz);
        clazz.addNode(GenericReportEntry.line, line01);
        clazz.addNode(GenericReportEntry.line, line02);
        clazz.addNode(GenericReportEntry.line, line03);
    }

    @Test
    public void testNameFilter(){
        Set<? extends Node>nodes = report.getNodes(new NameFilter(REPORT_NAME));
        assertTrue("Did not return any node", nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), report);
    }
}
