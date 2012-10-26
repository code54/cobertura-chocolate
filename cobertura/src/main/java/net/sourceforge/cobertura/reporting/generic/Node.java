package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;
import net.sourceforge.cobertura.reporting.generic.filter.Relation;

import java.util.Set;

public interface Node {
    /**
     * Add node with relation.
     * @param relation
     * @param node
     */
    void addNode(Relation relation, Node node);

    Set<Relation>getRelations();

    /**
     * Returns all nodes this node is immediately related to;
     * @return
     */
    Set<? extends Node>getNodes(boolean thisNodeIncluded);

    /**
     * Applies filter to current node and returns filtering result;
     * @param filter
     * @return
     */
    Set<? extends Node> getNodes(boolean thisNodeIncluded, Filter filter);

    /**
     * Applies filter to current node and recursively to all referenced nodes;
     * @param filter
     * @return
     */
    Set<? extends Node>getAllNodes(boolean thisNodeIncluded, Filter filter);

    /**
     * Returns all immediate nodes for specified relation;
     * @param relation
     * @return
     */
    Set<? extends Node> getNodesForRelation(Relation relation);

    /**
     * Get nodes name.
     * @return
     */
    String getName();

    /**
     * What does it represent. Ex.: class, method, line.
     * @return
     */
    NodeType getType();

    /**
     * Returns nodes Payload
     * @return
     */
    Payload getPayload();

    /**
     * Sets nodes Payload
     * @param payload
     */
    void setPayload(Payload payload);

}
