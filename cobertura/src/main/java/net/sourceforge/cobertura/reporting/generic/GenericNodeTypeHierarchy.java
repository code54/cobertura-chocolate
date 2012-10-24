package net.sourceforge.cobertura.reporting.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Will create a new NodeTypeHierarchy from the highest level to the first mismatch
 * between two NodeTypeHierarchy instances.
 */

/*
 * TODO may be useful when dealing with multiple langs in same report.
 * May need to add a provider or method getNodeTypeHierarchy(Node) so that node asks recursively.
 * If all nodes provide same lang provider, we use it, otherwise we combine them into a Generic one.
 * generalize this to accept a Set of hierarchies...
 */
public class GenericNodeTypeHierarchy extends AbstractNodeTypeHierarchy{

    private GenericNodeTypeHierarchy(){}

    public GenericNodeTypeHierarchy(NodeTypeHierarchy aHierarchy, NodeTypeHierarchy anotherHierarchy){
        NodeType ah = aHierarchy.getHighest();
        NodeType an = anotherHierarchy.getHighest();
        levels = new ArrayList<NodeType>();

        while(ah.equals(an)){
            levels.add(ah);
            ah = aHierarchy.getLower(ah);
            an = anotherHierarchy.getLower(an);
        }
    }
}
