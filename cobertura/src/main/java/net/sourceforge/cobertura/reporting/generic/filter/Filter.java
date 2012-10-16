package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;

import java.util.Set;

/**
 * Defines a condition to filter nodes.
 */
public interface Filter {
    /**
     * Filters given node and returns a Set with that node if not filtered
     * or an empty node if does not match given criteria.
     * @param node
     * @return
     */
    Set<? extends Node> filter(Node node);

    /**
     * Filters given nodes and returns a new Set with the ones that match a given criteria.
     * @param nodes
     * @return
     */
    Set<? extends Node> filter(Set<? extends Node> nodes);
}
