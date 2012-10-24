package net.sourceforge.cobertura.reporting.generic;

public interface NodeTypeHierarchy {

    /**
     * Returns the immediate higher level or null if is the highest one.
     * @param type
     * @return
     */
    NodeType getHigher(NodeType type);

    /**
     * Returns the immediate lower level or null if is the lowest one.
     * @param type
     * @return
     */
    NodeType getLower(NodeType type);

    /**
     * Compares two NodeTypes.
     * @param first
     * @param second
     * @return
     *      - returns 1 if first is higher than the second;
     *      - returns -1 if first is lower than second
     *      - returns 0 if are same;
     * @throws RuntimeException if no matching NodeType is found.
     */
    int compare(NodeType first, NodeType second);

    NodeType getHighest();
    NodeType getLowest();
}
