package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.BaseNode;
import net.sourceforge.cobertura.reporting.generic.CoverageData;
import net.sourceforge.cobertura.reporting.generic.GenericReportEntry;
import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FilterTest {

    private Map<String, Node>nodes;

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
        nodes = new HashMap<String, Node>();

        nodes.put(REPORT_NAME, new BaseNode(GenericReportEntry.report, REPORT_NAME));
        nodes.put(PROJECT_NAME, new BaseNode(GenericReportEntry.project, PROJECT_NAME));
        nodes.put(PACKAGE_NAME, new BaseNode(GenericReportEntry.packag, PACKAGE_NAME));
        nodes.put(SOURCE_NAME, new BaseNode(GenericReportEntry.sourcefile, SOURCE_NAME));
        nodes.put(CLASS_NAME, new BaseNode(GenericReportEntry.clazz, CLASS_NAME));
        nodes.put(LINE01_NAME, new BaseNode(GenericReportEntry.line, LINE01_NAME));
        nodes.put(LINE02_NAME, new BaseNode(GenericReportEntry.line, LINE02_NAME));
        nodes.put(LINE03_NAME, new BaseNode(GenericReportEntry.line, LINE03_NAME));

        nodes.get(REPORT_NAME).addNode(GenericReportEntry.project, nodes.get(PROJECT_NAME));
        nodes.get(PROJECT_NAME).addNode(GenericReportEntry.packag, nodes.get(PACKAGE_NAME));
        nodes.get(PACKAGE_NAME).addNode(GenericReportEntry.sourcefile, nodes.get(SOURCE_NAME));
        nodes.get(SOURCE_NAME).addNode(GenericReportEntry.clazz, nodes.get(CLASS_NAME));
        nodes.get(CLASS_NAME).addNode(GenericReportEntry.line, nodes.get(LINE01_NAME));
        nodes.get(CLASS_NAME).addNode(GenericReportEntry.line, nodes.get(LINE02_NAME));
        nodes.get(CLASS_NAME).addNode(GenericReportEntry.line, nodes.get(LINE03_NAME));
    }

    @Test
    public void testNameFilter(){
        Set<? extends Node>nodes = this.nodes.get(REPORT_NAME).getNodes(true, new NameFilter(new EqCriteria(REPORT_NAME)));
        assertTrue("Did not return any node", !nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), this.nodes.get(REPORT_NAME));
    }

    @Test
    public void testTypeFilter(){
        Set<? extends Node>nodes = this.nodes.get(REPORT_NAME).getNodes(true, new TypeFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: "+nodes.size(), nodes.isEmpty());

        nodes = this.nodes.get(REPORT_NAME).getAllNodes(true, new TypeFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: " + nodes.size(), nodes.size() == 3);
    }

    @Test
    public void testRelationFilter(){
        Set<? extends Node>nodes = this.nodes.get(REPORT_NAME).getNodes(true, new RelationFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: "+nodes.size(), nodes.isEmpty());

        nodes = this.nodes.get(REPORT_NAME).getAllNodes(true, new TypeFilter(new EqCriteria(GenericReportEntry.line)));
        assertTrue("Did not return the expected nodes: "+nodes.size(), nodes.size()==3);
    }

    @Test
    public void testCompositeFilter(){
        List<Filter> filters = new ArrayList<Filter>();
        Filter nameFilter = new NameFilter(new EqCriteria(REPORT_NAME));
        filters.add(nameFilter);
        Filter relationFilter = new RelationFilter(new EqCriteria(GenericReportEntry.project));
        filters.add(relationFilter);

        Set<? extends Node>nodes = this.nodes.get(REPORT_NAME).getNodes(true, nameFilter);
        assertTrue("Did not return any node", !nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), this.nodes.get(REPORT_NAME));

        nodes = this.nodes.get(REPORT_NAME).getNodes(true, relationFilter);
        assertTrue("Did not return any node", !nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), this.nodes.get(PROJECT_NAME));

        nodes = this.nodes.get(REPORT_NAME).getNodes(true, new CompositeFilter(filters));
        assertTrue("Returned empty set while expecting nodes", !nodes.isEmpty());
        assertEquals("Did not return expected node", nodes.iterator().next(), this.nodes.get(PROJECT_NAME));
    }
}
