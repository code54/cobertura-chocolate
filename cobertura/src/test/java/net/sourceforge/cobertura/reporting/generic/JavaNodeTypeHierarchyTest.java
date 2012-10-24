package net.sourceforge.cobertura.reporting.generic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaNodeTypeHierarchyTest {
    @Test
    public void testGetHighest() throws Exception {
        NodeTypeHierarchy hierarchy = new JavaNodeTypeHierarchy();
        assertEquals(NodeType.PROJECT, hierarchy.getHighest());
    }

    @Test
    public void testGetLowest() throws Exception {
        NodeTypeHierarchy hierarchy = new JavaNodeTypeHierarchy();
        assertEquals(NodeType.LINE, hierarchy.getLowest());
    }

    @Test
    public void testGetLower() throws Exception {
        NodeTypeHierarchy hierarchy = new JavaNodeTypeHierarchy();
        assertEquals(NodeType.CLASS, hierarchy.getLower(NodeType.SOURCE));
        assertEquals(NodeType.METHOD, hierarchy.getLower(NodeType.CLASS));
        assertEquals(NodeType.LINE, hierarchy.getLower(NodeType.METHOD));
    }

    @Test
    public void testLowerHigherConsistent() throws Exception {
        NodeTypeHierarchy hierarchy = new JavaNodeTypeHierarchy();
        NodeType highest = hierarchy.getHighest();
        NodeType lowest = hierarchy.getLowest();
        List<NodeType> lower = new ArrayList<NodeType>();
        lower.add(highest);
        List<NodeType> higher = new ArrayList<NodeType>();
        higher.add(lowest);
        NodeType higherThanPrev = hierarchy.getHigher(lowest);
        NodeType lowerThanPrev = hierarchy.getLower(highest);
        while((higherThanPrev !=null)&&(lowerThanPrev !=null)){
            if((lowerThanPrev = hierarchy.getLower(lower.get(lower.size()-1))) !=null){
                lower.add(lowerThanPrev);
            }
            if((higherThanPrev = hierarchy.getHigher(higher.get(higher.size()-1)))!=null){
                higher.add(higherThanPrev);
            }
        }
        assertEquals(lower.size(), higher.size());
        for(int j=0;j<lower.size();j++){
            assertEquals(lower.get(j), higher.get(higher.size()-j-1));
        }
    }
}
