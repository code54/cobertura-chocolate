package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.BaseNode;
import net.sourceforge.cobertura.reporting.generic.CoverageData;
import net.sourceforge.cobertura.reporting.generic.GenericReportEntry;
import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
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
        report = new BaseNode(GenericReportEntry.report, REPORT_NAME);

        Node project = new BaseNode(GenericReportEntry.project, PROJECT_NAME);

        Node packag = new BaseNode(GenericReportEntry.packag, PACKAGE_NAME);

        Node source = new BaseNode(GenericReportEntry.sourcefile, SOURCE_NAME);

        Node clazz = new BaseNode(GenericReportEntry.clazz, CLASS_NAME);

        Node line01 = new BaseNode(GenericReportEntry.line, LINE01_NAME);

        Node line02 = new BaseNode(GenericReportEntry.line, LINE02_NAME);

        Node line03 = new BaseNode(GenericReportEntry.line, LINE03_NAME);

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
        Set<? extends Node>nodes = report.getNodes(true, new NameFilter(new EqCriteria(REPORT_NAME)));
        assertTrue("Did not return any node", !nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), report);
    }

    @Test
    public void testTypeFilter(){
        Set<? extends Node>nodes = report.getNodes(true, new TypeFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: "+nodes.size(), nodes.isEmpty());

        nodes = report.getAllNodes(true, new TypeFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: "+nodes.size(), nodes.size()==3);
    }
}
