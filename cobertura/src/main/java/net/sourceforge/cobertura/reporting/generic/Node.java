package net.sourceforge.cobertura.reporting.generic;
/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
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
