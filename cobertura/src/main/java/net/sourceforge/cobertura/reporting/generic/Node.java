package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;

import java.util.Set;

public interface Node {
    /**
     * Add node with relation.
     * @param relation
     * @param node
     */
    void addNode(String relation, Node node);

    Set<String>getRelations();

    /**
     * Returns all nodes this node is immediately related to;
     * @return
     */
    Set<? extends Node>getNodes(boolean thiNodeIncluded);

    /**
     * Applies filter to current node and returns filtering result;
     * @param filter
     * @return
     */
    Set<? extends Node> getNodes(boolean thiNodeIncluded, Filter filter);

    /**
     * Applies filter to current node and recursively to all referenced nodes;
     * @param filter
     * @return
     */
    Set<? extends Node>getAllNodes(boolean thiNodeIncluded, Filter filter);

    /**
     * Returns all immediate nodes for specified relation;
     * @param relation
     * @return
     */
    Set<? extends Node> getNodesForRelation(String relation);

    /**
     * Get nodes name.
     * @return
     */
    String getName();

    /**
     * What does it represent. Ex.: class, method, line.
     * @return
     */
    String getType();

}
